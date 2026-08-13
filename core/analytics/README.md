# `:core:analytics`

Small, flavor-aware analytics layer. Call sites talk to an interface; Gradle + Hilt pick the real implementation at compile time (`demo` vs `prod`).

PokeTCG is a pet project for studying current Android architecture. This module exists so we can practice a pattern that scales — not because we need a custom analytics stack.

## Why this exists

Feature code should never know *how* events are shipped. It should only know *that* an event happened:

```kotlin
analyticsHelper.logEvent(
    AnalyticsEvent(
        type = AnalyticsEvent.Types.SCREEN_VIEW,
        extras = listOf(
            AnalyticsEvent.Param(AnalyticsEvent.ParamKeys.SCREEN_NAME, "card_detail"),
        ),
    ),
)
```

The backend (Firebase, logcat, nothing) is a **build-time** choice, not a runtime `if (BuildConfig.DEBUG)` sprinkled across the app.

## Product flavors (demo vs prod)

Flavors are **not** defined in this module's `build.gradle.kts`. They come from convention plugins so every Android module shares the same `tier` dimension.

Defined in:

[`build-logic/convention/src/main/java/com/lfgtavora/poketcg/build_logic/convention/Flavors.kt`](../../build-logic/convention/src/main/java/com/lfgtavora/poketcg/build_logic/convention/Flavors.kt)

Applied automatically by:

- [`AndroidLibraryConventionPlugin`](../../build-logic/convention/src/main/java/AndroidLibraryConventionPlugin.kt) — this module uses `poketcg.android.library`
- [`AndroidApplicationConventionPlugin`](../../build-logic/convention/src/main/java/AndroidApplicationConventionPlugin.kt) — the `:app` module

```kotlin
enum class PokeTcgFlavor(val applicationIdSuffix: String? = null) {
    demo(".demo"),
    prod,
}

fun configureFlavors(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    commonExtension.apply {
        flavorDimensions += "tier"
        productFlavors {
            PokeTcgFlavor.values().forEach { flavor ->
                create(flavor.name) {
                    dimension = "tier"
                    // app module only: demo APK is a different package (…poketcg.demo)
                    if (this@apply is ApplicationExtension && this is ApplicationProductFlavor) {
                        flavor.applicationIdSuffix?.let { applicationIdSuffix = it }
                    }
                }
            }
        }
    }
}
```

Gradle then maps source sets 1:1:

| Flavor | Source set | What gets compiled |
| --- | --- | --- |
| `demo` | `src/demo/` | `StubAnalyticsHelper` + demo `AnalyticsModule` |
| `prod` | `src/prod/` | `FirebaseAnalyticsHelper` + prod `AnalyticsModule` |
| both | `src/main/` | interface, event model, NoOp, Compose local |

`src/demo` and `src/prod` can contain classes with the **same fully-qualified name**. Gradle includes only one of them per variant, so there is never a clash. That is how we ship two `AnalyticsModule` classes without `if`/`when` on flavor.

You can also declare flavor-scoped dependencies (`prodImplementation`, `demoImplementation`). Firebase belongs on `prod` only — it should not land in demo APKs. Those lines are currently commented in `build.gradle.kts` until Firebase is wired for real.

### Why this is a solid swap strategy

Runtime flags (`if (DEBUG)`, Remote Config, a boolean in DI) leak production SDKs into every build and force every caller to remember the branch.

Flavor source sets + Hilt bindings give you:

1. **Compile-time substitution** — demo never compiles Firebase helper code.
2. **No production telemetry from local/debug APKs** — demo writes to logcat; prod talks to Firebase.
3. **Call sites stay dumb** — inject `AnalyticsHelper`, done.
4. **Smaller / safer demo artifacts** — no analytics SDK, no extra Google services init, different `applicationId` so demo and prod can sit on the same device.
5. **Same pattern for other dual implementations** — fake network, stub billing, mock auth. Analytics is the smallest example of the idea.

Trade-off: you maintain two source sets. For a backend you do **not** want in 90% of daily builds, that cost is worth it.

## Hilt's role

Flavors decide *which files exist*. Hilt decides *what gets injected*.

Both flavors install a module into `SingletonComponent` and `@Binds` a concrete type to `AnalyticsHelper`:

**demo** (`src/demo/.../AnalyticsModule.kt`):

```kotlin
@Binds
abstract fun bindsAnalyticsHelper(analyticsHelperImpl: StubAnalyticsHelper): AnalyticsHelper
```

**prod** (`src/prod/.../AnalyticsModule.kt`):

```kotlin
@Binds
abstract fun bindsAnalyticsHelper(analyticsHelperImpl: FirebaseAnalyticsHelper): AnalyticsHelper
```

Prod also `@Provides` `FirebaseAnalytics`. Demo never sees that provider.

A `@HiltViewModel` (or any `@Inject` constructor) asks for `AnalyticsHelper`. The graph has exactly one binding for the variant you built. Swap flavor → rebuild → different graph. No `when (flavor)` in feature code.

`StubAnalyticsHelper` is `@Singleton` + `@Inject`. `FirebaseAnalyticsHelper` is constructed by Hilt with the provided `FirebaseAnalytics`. Both are `internal` — features should not import them.

## Types in `src/main`

| Type | Role |
| --- | --- |
| `AnalyticsHelper` | The only type features should depend on. |
| `AnalyticsEvent` | `type` + `extras` (`Param` key/value). Prefer `Types` / `ParamKeys`; custom names are fine if the backend is configured for them. |
| `StubAnalyticsHelper` | Demo: dump events to logcat. You can verify tracking without a dashboard. |
| `NoOpAnalyticsHelper` | Tests / Compose previews: swallow events. Not the demo implementation. |
| `LocalAnalyticsHelper` | `CompositionLocal` so composables can log without threading a helper through every parameter. Default is `NoOpAnalyticsHelper`, so previews do not crash. Real UI should `CompositionLocalProvider` the injected helper at the app root. |

Prod-only:

| Type | Role |
| --- | --- |
| `FirebaseAnalyticsHelper` | Forwards to Firebase. Truncates keys (40) and values (100) to match Firebase limits. |

`Stub` vs `NoOp` is intentional: demo should still *show* that events fire; tests/previews should be silent.

## How to use it

```kotlin
@HiltViewModel
class CardDetailViewModel @Inject constructor(
    private val analyticsHelper: AnalyticsHelper,
) : ViewModel() { /* … */ }
```

Compose (after providing the local at the app root):

```kotlin
val analyticsHelper = LocalAnalyticsHelper.current
```

Do not add methods to `AnalyticsHelper` and do not call Firebase from a feature module. Screen vs click is the usual fork:

**Screen** stays in the core (`Types.SCREEN_VIEW`). It is a Firebase recommended event and every destination logs the same shape.

**Click / domain actions** stay in the **feature**, as an extension — same pattern NIA uses for `news_resource_opened`, search, bookmarks. The core does not need a `CLICK` type:

```kotlin
fun AnalyticsHelper.logCardClicked(cardId: String, setId: String) {
    logEvent(
        AnalyticsEvent(
            type = "card_clicked",
            extras = listOf(
                AnalyticsEvent.Param("card_id", cardId),
                AnalyticsEvent.Param("set_id", setId),
            ),
        ),
    )
}
```

Call it from the ViewModel or `LocalAnalyticsHelper.current`. `FirebaseAnalyticsHelper` already forwards `type` + `extras`; demo dumps to logcat.

Only put a generic type in `AnalyticsEvent.Types` if the event is truly transversal (e.g. Firebase `select_content` with `content_type` / `item_id`). Prefer named feature events for funnels. Do not wrap every `onClick` — track actions you will actually look at.

## Mental model

```
Feature / Compose
        │  AnalyticsHelper.logEvent(...)
        ▼
   Hilt binding  ← flavor-specific AnalyticsModule
        │
        ├─ demo  → StubAnalyticsHelper  → Logcat
        └─ prod  → FirebaseAnalyticsHelper → Firebase
```

Gradle picks the source set. Hilt picks the binding. Features never see either decision.

## Credit / why we did not invent this

This module is copied from [Now in Android (NIA)](https://github.com/android/nowinandroid) (`core:analytics`), including the flavor split, Hilt modules, event model, and Compose local.

NIA's version is the right default for ~99% of apps. There is no reason to fork the design: interface + flavor source sets + `@Binds` already covers production, demo, tests, and previews. When we eventually enable Firebase, we uncomment `prodImplementation` and keep the rest as-is.

If you are learning this repo, treat this module as a reference implementation of **flavor-based dependency substitution**, not as a unique analytics product.
