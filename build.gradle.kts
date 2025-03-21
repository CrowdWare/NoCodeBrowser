// Top-level build file where you can add configuration options common to all sub-projects/modules.
/*plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}
*/
plugins {
    alias(libs.plugins.android.application) apply false
    id("com.android.library") version "8.6.0" apply false
    alias(libs.plugins.kotlin.android) apply false
}