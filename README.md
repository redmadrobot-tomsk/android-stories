# Android-stories

[![Platform](https://img.shields.io/badge/platform-android-brightgreen)](https://github.com/redmadrobot-tomsk/android-stories) [![](https://jitpack.io/v/redmadrobot-tomsk/android-stories.svg)](https://jitpack.io/#redmadrobot-tomsk/android-stories) [![license](https://img.shields.io/github/license/mashape/apistatus.svg)](https://github.com/redmadrobot-tomsk/android-stories#license)

![open and close](https://user-images.githubusercontent.com/89060414/165884442-aae49dc4-38da-48b0-b38f-d7c9754be88b.gif)![templates](https://user-images.githubusercontent.com/89060414/165884447-b05b1a6a-df38-4f75-9157-f24da3cfac2c.gif)



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
    implementation 'com.github.redmadrobot-tomsk:android-stories:1.1.1'
}
```

## Initialization

1. Initialize StoriesCacheFactory in your `Application` class.

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

2. Add internet permission to your manifest for Glide.

```
<uses-permission android:name="android.permission.INTERNET" />
```

## Setting up stories activity

1. Extend `StoriesBaseActivity` and override its functions how you see fit. Note that you MUST start
   stories activity with intent returned by `StoriesBaseActivity.newStoriesIntent` because of
   required parameters (`StoriesInputParams`). Otherwise, exception will be thrown.

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

2. Create stories models (e.g. by fetching from your API and converting). For example, here is a
   simple story model:

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

3. Add stories to `StoriesController`.

```
val stories = listOf(story) // sample story was created in  p.2
val controller: StoriesController = StoriesCacheFactory.getInstance()
controller.clearAndAdd(StoriesConfig.All, stories)
```

4. Start `StoriesActivity` (don't forget to add required arguments to intent).

```
someButton.setOnClickListener {
    val intent = StoriesActivity.newIntent(
        context = this,
        storiesInputParams = StoriesInputParams.createDefaults()
    )
    startActivity(intent)
}
```

## Setting up previews

1. Add RecyclerView to your activity/fragment layout that should open `StoriesActivity`.

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

2. In your activity/fragment (e.g. `MainActivity`), create stories previews adapter, assign it to
   the `RecyclerView`, and implement `StoriesAdapterListener` interface to open StoriesActivity (
   see "Setup Stories activity" section).

```
class MainActivity : AppCompatActivity(), StoriesBasePreviewAdapter.StoriesAdapterListener {

    private val storiesAdapter = StoriesPreviewAdapter(this@MainActivity)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<RecyclerView>(R.id.recyclerStories).adapter = storiesAdapter
    }

    override fun onStoryClicked(storiesInputParams: StoriesInputParams) {
        val intent = StoriesActivity.newIntent(
            context = this,
            storiesInputParams = storiesInputParams
        )
        startActivity(intent)
    }
}
```

(note that you can create your own stories previews adapter by
extending `StoriesBasePreviewAdapter`).

## Custom story frame view

It's possible to use a different story frame layout if you wish to change it.
`StoryFrameViewImpl` is used by default.

1. Create your own story frame view by extending `BaseStoryFrameView`. You should set data to your
   views and update them in `onFrameSet`.

```
class CustomStoryFrameView(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseStoryFrameView(context, attrs, defStyleAttr) {

    init {
        View.inflate(context, R.layout.view_custom_frame, this)
    }

    private val textTitle = findViewById<TextView>(R.id.textTitle)

    override fun onFrameSet(frame: StoryFrame) {
        textTitle.text = frame.content.header1
    }
}
```

view_custom_story.xml:

```
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <TextView
        android:id="@+id/textTitle"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:textAlignment="center"/>

</FrameLayout>
```

2. Extend `StoryFragment` and override `createStoryFrameView`, where you should return
   your `BaseStoryFrameView` implementation. Note that you MUST pass story instance when creating
   your custom fragment by calling `StoryFragment#addStoryToArguments`, otherwise, exception will be
   thrown (similar to `StoriesBaseActivity` and `StoriesInputParams`).

```
class CustomStoryFragment : StoryFragment() {
    companion object {
        fun newInstance(story: Story): StoryFragment =
            CustomStoryFragment().addStoryToArguments(story)
    }

    override fun createStoryFrameView(context: Context): BaseStoryFrameView =
        CustomStoryFrameView(context)
}
```

4. Override `createStoriesFragment` in your stories activity derived from `StoriesActivity` like
   this:

```
class StoriesActivity : StoriesBaseActivity() {
    override val createStoriesFragment: ((Story) -> StoryFragment) = { story ->
        CustomStoryFragment.newInstance(story)
    }
}
```

5. You can change the behaviour of "is seen" status for the story by passing `StoryIsSeenWhen`
   to `StoryFragment#addToStoryArguments`.
   See  [StoryIsSeenWhen](https://github.com/redmadrobot-tomsk/android-stories/tree/master/app/src/main/java/com/redmadrobot/stories/models/StoryIsSeenWhen.kt)
   for more details.

```
class StoriesActivity : StoriesBaseActivity() {
    override val createStoryFragment: ((Story) -> StoryFragment) = { story ->
        StoryFragment.newStoryFragmentInstance(story, StoryIsSeenWhen.TWO)
    }
}
```

For more info,
see [the example](https://github.com/redmadrobot-tomsk/android-stories/tree/master/app/src/main/java/com/redmadrobot/example/custom)
.

## License

The library is distributed under the MIT LICENSE. See LICENSE for details.
