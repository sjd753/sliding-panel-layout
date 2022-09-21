# Sliding Panel Layout ![version](https://jitpack.io/v/sjd753/sliding-panel-layout.svg)

A lightweight library to add sliding panel behavior to any layout or fragment in your project!

### Latest version: (https://jitpack.io/#sjd753/sliding-panel-layout)

![preview](https://github.com/sjd753/sliding-panel-layout/blob/master/sample/sample-1.gif)

## How to use 

### Step 1. Add the JitPack repository to your settings.gradle or build.gradle file

Add it in your root settings.gradle at the end of repositories:

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven { url 'https://jitpack.io' }
    }
}
```
OR

For older projects
Add it in your root build.gradle at the end of repositories:

```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
 
### Step 2. Add the dependency

```
dependencies {
	implementation 'com.github.sjd753:sliding-panel-layout:Tag'
}
```
### Step 3. Add SlidingPanelLayout in layout
Note: SlidingPanelLayout can contain only one child layout or fragment 

```xml
    <com.sjd.library.SlidingPanelLayout
        android:id="@+id/slidingPanelLayout"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:layout_behavior="@string/appbar_scrolling_view_behavior">

        <include layout="@layout/content_main" />

    </com.sjd.library.SlidingPanelLayout>
```
### Usage in code

```kotlin
        /**
        * Set the sliding panel direction either DIRECTION_RIGHT_PANEL or DIRECTION_LEFT_PANE
        * Default direction is DIRECTION_RIGHT_PANEL
        *
        * @param panelSlideDirect Direction for the sliding panel layout
        */
        fun setPanelSlideDirect(@Direction panelSlideDirect: Int) 
        binding.slidingPanelLayout.setPanelSlideDirect(SlidingPanelLayout.DIRECTION_RIGHT_PANEL);

        /**
        * Set the sliding panel peak percentage
        * Default peak is 50 percent of the content view
        *
        * @param percent Percentage for the sliding panel layout to peak at
        */
        fun setPeakAt(percent: Int)
        binding.slidingPanelLayout.setPeakAt(percent = 33);

        /**
        * Set the sliding panel callback listener
        */
        binding.slidingPanelLayout.setCallBack(object : SlidingPanelLayout.CallBack {
            override fun onViewPanelStateChanged(state: Int) {
                
            }

            override fun onViewDragStateChanged(state: Int) {

            }
        })

        /**
        * Set or get the panel state
        */
        private fun toggleSlidingPanelState() {
            val panelState = binding.slidingPanelLayout.panelState
            binding.slidingPanelLayout.panelState =
                if (panelState == SlidingPanelLayout.STATE_OPEN)
                    SlidingPanelLayout.STATE_PEAK
                else SlidingPanelLayout.STATE_OPEN
        }
```
	

## Authors

* **sjd753** - *sjd753@gmail.com* 

* Thanks!
