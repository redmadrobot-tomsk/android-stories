# Android-stories

A simple stories library inspired by Instagram and alike. 

## Requirements

- Min SDK >= 22

## Installation

Add these dependencies to your project:

```
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.redmadrobot-tomsk:android-stories:1.0.1'
}
```

## Initialization

1. Initialize StoriesCacheFactory in your Application class.

```
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        StoriesCacheFactory
            .init(this, CoroutineScope(Dispatchers.IO + SupervisorJob()))
            .preloadImages(false)
            .setConfig(StoriesConfig.All)
    }
}
```

2. Add internet permission to your manifest.

```
<uses-permission android:name="android.permission.INTERNET" />
```

## Setting up stories activity

1. Extend StoriesBaseActivity and override its functions how you see fit.
Note that you MUST start stories activity with intent returned by StoriesBaseActivity.newStoriesIntent because of required parameters (StoriesInputParams). 
Otherwise, exception will be thrown.

```
class StoriesActivity : StoriesBaseActivity() {

    companion object {
        fun newIntent(
            context: Context,
            storiesInputParams: StoriesInputParams
        ): Intent = newStoriesIntent(
            context = context,
            clazz = StoriesActivity::class.java,
            storiesInputParams = storiesInputParams
        )
    }
    
    override fun onStoryActionClicked(url: String) {
        // TODO: Your implementation.
    }

    override fun closeStories() {
        super.closeStories()
    }

    override fun onCompleteStory() {
        super.onCompleteStory()
    }

    override fun hasPreviousStory(story: Story): Boolean {
        return super.hasPreviousStory(story)
    }
}
```

2. Create stories models (e.g. by fetching from your API and converting).
For example, here is a simple story model:
```
val story = Story(
    id = "some-UUID-or-else",
    name = "story name",
    isSeen = false,
    previewUrl = "https://your-api.com/preview-image-url",
    title = "title",
    frames = listOf(
        StoryFrame(
            imageUrl = "https://your-api.com/frame-image-url",
            content = StoryFrameContent(
                controlsColor = StoryFrameControlsColor.DARK, // Color for progress and close button.
                showGradients = StoryFrameShowGradients.BOTTOM, // Where to show gradient.
                position = StoryFrameContentPosition.TOP, // Position of contents relative to the StoryFrame.
                textColor = "#FFFFFF",
                header1 = "First story frame header",
                header2 = "Second story frame header",
                descriptions = listOf("First line of description", "Second line of description"),
                action = StoryFrameAction(
                    text = "Text to be displayed on clickable action button",
                    url = "https://your-api.com/deep-link-or-url"
                ),
                gradientColor = "#000000"
            )
        )
    )
)
```

3. Add stories to StoriesController.

```
val stories = listOf(story) // sample story was created in  p.3
val controller: StoriesController = StoriesCacheFactory.getInstance()
controller.clearAndAdd(StoriesConfig.All, stories)
```

4. Start StoriesActivity (don't forget to add required arguments to intent).

```
someButton.setOnClickListener {
    val intent = StoriesActivity.newIntent(
        context = this,
        storiesInputParams = StoriesInputParams.createDefaults()
    )
    // Optional animations
    AnimationUtils.setExitTransition(this, R.transition.stories_transition)
    val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
    startActivity(intent, options)
}
```

## Setting up previews

1. Add RecyclerView to your activity/fragment layout that should open StoriesActivity.

```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent">
    
    <!-- Some views here -->

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/recyclerStories"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager" />

    <!-- Some views there -->

</LinearLayout>
```

2. In your activity/fragment (e.g. MainActivity), create stories previews adapter, assign it to the RecyclerView, and implement StoriesAdapterListener interface to open StoriesActivity (see "Setup Stories activity" section).

```
class MainActivity : AppCompatActivity(), StoriesBasePreviewAdapter.StoriesAdapterListener {

    private val storiesAdapter = StoriesPreviewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        findViewById<RecyclerView>(R.id.recyclerStories).adapter = storiesAdapter
    }

    override fun onStoryClicked(storiesInputParams: StoriesInputParams) {
        AnimationUtils.setExitTransition(this, R.transition.stories_transition)
        val intent = StoriesActivity.newIntent(
            context = this,
            storiesInputParams = storiesInputParams
        )
        // Optional animations
        val options = ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        startActivity(intent, options)
    }
}
```

(note that you can create your own stories previews adapter by extending StoriesBasePreviewAdapter).

## License

The library is distributed under the MIT LICENSE. See LICENSE for details.
