# StoryView
StoryView aar is to be used by developers who need to add story like views to their applications.
Designed using kotlin lifecycle scope and flow
I used kotlin coroutine as oppose to threads because it is easier to track and cancel jobs as oppose to cancelling threads.

[![](https://jitpack.io/v/D-Ordnance/story-view.svg)](https://jitpack.io/#D-Ordnance/story-view)
This version does not supports videos yet,
as it is in progress and would be released in the nearest future.

## Table of Content
- How to install
- Features
- Code Snippet
- Output

### How to install
Add this jitpack repo reference to your settings.gradle
```
maven { url 'https://jitpack.io' }
```
then add the dependency to your app build.gradle file
```
implementation 'com.github.D-Ordnance:story-view:{version}'
```
### Features
* You can pause story
* Continue story
* Move to next item in a story
* and Move to previous item in a story
### Code Snippet
```
Your XML:

<deeosoft.library.StoryView
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:storyIndicatorViewMarginEnd="12dp"
        app:storyLayoutMarginLeft="22dp"
        app:storyIndicatorViewHeight="6dp"
        app:storyLayoutMarginRight="22dp"
        app:storyLayoutMarginTop="150dp"
        app:storyFragments="@array/fragments"
        app:storyIndicatorViewProgressDrawable="@drawable/story_progress_indicator_2"
        app:storyIndicatorViewDelay="10"
        android:id="@+id/storyView"/>
        
app:storyFragemnts value is a refernce to an array of layout reference
e.g. 
<resources>
    <integer-array name="fragments">
        <item>@layout/story_one</item>
        <item>@layout/story_one</item>
        <item>@layout/story_one</item>
        <item>@layout/story_one</item>
        <item>@layout/story_one</item>
        <item>@layout/story_one</item>
        <item>@layout/story_one</item>
        <item>@layout/story_one</item>
    </integer-array>
</resources>
```
A listener that captures when story is done
```
implement OnStoryActionListener
```

finally start the story
```
storyView.startStory()
```
### Output
https://user-images.githubusercontent.com/14176513/201533117-c5cb694c-18ce-46d3-827e-695417e0c95b.mp4

