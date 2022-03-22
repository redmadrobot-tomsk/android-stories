package com.redmadrobot.example

import com.redmadrobot.stories.models.*
import kotlin.random.Random

object StoriesCreator {

    private val random = Random(System.currentTimeMillis())

    fun createStories(): List<Story> {
        return listOf(
            createStoryFrameContent(
                id = "1",
                imgs = listOf(
                    "https://upload.wikimedia.org/wikipedia/ru/thumb/9/97/Philip_J._Fry.png/248px-Philip_J._Fry.png",
                    "https://i.pinimg.com/originals/89/6a/13/896a135e2fc82ea7ae953297427cb9d8.jpg"
                ),
                title = "Philip Fry"
            ),
            createStoryFrameContent(
                id = "2",
                imgs = listOf(
                    "https://autogear.ru/misc/i/gallery/40429/1249161.jpg"
                ),
                title = "Bender"
            ),
            createStoryFrameContent(
                id = "3",
                imgs = listOf(
                    "https://upload.wikimedia.org/wikipedia/ru/d/d4/Turanga_Leela.png",
                    "https://citaty.info/files/characters/79532.jpg"
                ),
                title = "Leela"
            ),
            createStoryFrameContent(
                id = "4",
                imgs = listOf(
                    "https://deti-online.com/img/kak-narisovat-zoydberga-iz-futuramy-color.jpg",
                    "https://citaty.info/files/characters/15914.jpg"
                ),
                title = "Zoyberg"
            )
        )
    }

    private fun createStoryFrameContent(id: String, imgs: List<String>, title: String): Story =
        Story(
            id = id,
            name = "name$id",
            isSeen = random.nextBoolean(),
            previewUrl = imgs[0],
            title = title,
            frames = imgs.mapIndexed { index, img ->
                StoryFrame(
                    imageUrl = img,
                    content = StoryFrameContent(
                        controlsColor = if (random.nextBoolean()) StoryFrameControlsColor.DARK else StoryFrameControlsColor.LIGHT,
                        showGradients = if (random.nextBoolean()) StoryFrameShowGradients.BOTTOM else StoryFrameShowGradients.TOP,
                        position = if (random.nextBoolean()) StoryFrameContentPosition.BOTTOM else StoryFrameContentPosition.TOP,
                        textColor = if (random.nextBoolean()) "#FFFFFF" else "#FF0000",
                        header1 = title,
                        header2 = "$title $index",
                        descriptions = listOf("Description $index", "Description 22"),
                        action = StoryFrameAction(
                            text = "Action $index",
                            url = ""
                        ),
                        gradientColor = "#000000"
                    )
                )
            }
        )
}
