/*
 * Copyright 2022. Explore in HMS. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hms.explorelandmarks.ui.landmark_detail

import com.hms.explorelandmarks.R
import com.hms.explorelandmarks.data.model.Landmark
import com.hms.explorelandmarks.di.ResourceProvider
import com.hms.explorelandmarks.utils.State
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class LandmarksRepositoryImpl @Inject constructor(
    private val resourceProvider: ResourceProvider
) : ILandmarksRepository {

    override suspend fun getLandmarkInformation(recognizedLandmark: Landmark): Flow<State<Landmark>> = flow {
        emit(State.Loading)

        delay(2000) // imitate web request

        val landmarkSearchResult = filterLandmarksByLandmarkName(recognizedLandmark.landmarkName)

        val resultLandmark = landmarkSearchResult?.copy(
            location = recognizedLandmark.location,
            landmarkRecognizedImage = recognizedLandmark.landmarkRecognizedImage
        ) ?: run {
            recognizedLandmark.copy(
                landmarkDescription = resourceProvider.getString(
                    R.string.error_no_lanmark_description
                ),
                landmarkRecognizedImage = recognizedLandmark.landmarkRecognizedImage
            )
        }

        emit(State.Success(resultLandmark))
    }

    private fun filterLandmarksByLandmarkName(landmarkName: String): Landmark? {
        return getLandmarksList().filter {
            it.landmarkName.equals(landmarkName, ignoreCase = true) ||
                it.landmarkDescription.contains(
                    landmarkName,
                    ignoreCase = true
                )
        }.firstOrNull()
    }

    // MOCK LANDMARKS
    private fun getLandmarksList() = listOf(
        Landmark(
            "Eiffel Tower",
            "Location: Paris,France\n\n" +
                "The tower is 324 metres (1,063 ft) tall, about the same height as an 81-storey building, and the tallest structure in Paris.\nThe Eiffel Tower was built to be one the main attractions at the Paris World's Fair in 1889. That year, the World's Fair covered the entire Champ de Mars in Paris and its focus was the vast constructions in iron and steel that were the great industrial advancement of that time.",
            landmarkPanoramicImage = if (Random.nextInt() % 2 == 0) R.raw.eiffel_tower_image else R.raw.eiffel_tower_image_2,
            landmarkPanoramicVideo = R.raw.eiffel_tower_video_360
        ),
        Landmark(
            "Sydney Opera House",
            "Location: Sydney, Australia\n\n" +
                "The Sydney Opera House is one of Australia's top tourist attractions and one of the world's most recognizable buildings. It's also known as one of the busiest performing arts venues in the world. To get to know the famous Opera House, take The Sydney Opera House Tour, a guided, hourlong journey that costs \$40 (about \$29 USD). Several additional guided options are also available, including a comprehensive backstage tour of the venue. Afterward, stay for drinks or dinner harborside at one of the venue's outdoor restaurants.",
            landmarkPanoramicImage = if (Random.nextInt() % 2 == 0) R.raw.opera_house_image else R.raw.opera_house_image_2
        ),
        Landmark(
            "Burj Khalifa",
            "Location:  Dubai, United Arab Emirates\n\n" +
                "Burj Khalifa is the tallest building and tallest free-standing structure in the world, measuring more than 2,716 feet high. This impressive architectural feat has more than 160 stories to its name, affording unforgettable views of Dubai below. Visitors will want to reserve tickets ahead of time for privileged access to the world's highest observation deck, At The Top, Burj Khalifa SKY. Then, head to the world's highest lounge, The Lounge, Burj Khalifa, for afternoon tea or Champagne at sunset.",
            landmarkPanoramicImage = if (Random.nextInt() % 2 == 0) R.raw.burj_khalifa_image else R.raw.burj_khalifa_image_2,
            landmarkPanoramicVideo = R.raw.burj_khalifa_video
        ),
        Landmark(
            "Kabah",
            "Location:  Al Haram, Makkah al-Mukarramah, Saudi Arabia\n\n" +
                "The Holy Kaaba ( Kabah ) is the most sacred place for Muslims. It is the house of Allah. Kaaba has other names as well such as Bait-al-Atiq, the Ancient House and Bait-al-Haram, The Sacred House. The Holy Kaaba is cubical in shape. It measures fifteen meters in height while on the sides it is ten and a half meters each side. Kaaba is covered with a large black cloth made of silk. It has verses of the Holy Quran embroidered on it with gold. This cloth is called kiswa. The cloth is manufactured in Saudi Arabia. It is changed every year.",
            landmarkPanoramicImage = if (Random.nextInt() % 2 == 0) R.raw.kaaba_image else R.raw.kaaba_image_1,
        )
    )
}
