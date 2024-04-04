package com.example.yassinesaddikimeteoapp.android.activity


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.yassinesaddikimeteoapp.android.R
import com.example.yassinesaddikimeteoapp.android.model.WeatherUiModel
import com.example.yassinesaddikimeteoapp.android.ui.theme.ComposeMVVMWeatherAppTheme
import com.example.yassinesaddikimeteoapp.android.utils.capitalize
import com.example.yassinesaddikimeteoapp.android.utils.getWeatherIcon
import com.example.yassinesaddikimeteoapp.android.utils.todayDate
import com.example.yassinesaddikimeteoapp.android.viewmodel.MainViewModel



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeMVVMWeatherAppTheme {
                CurrentWeatherHeader()
            }
        }
    }


}

@Composable
fun CurrentWeatherHeader(mainViewModel: MainViewModel = viewModel()) {
    Column{
        when (val state = mainViewModel.uiState.collectAsState().value) {
            is MainViewModel.WeatherUiState.Empty -> Text(
                text = stringResource(R.string.no_data_available),
                modifier = Modifier.padding(16.dp)
            )

            is MainViewModel.WeatherUiState.Loading -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WeatherLoadingAnimation()
            }


            is MainViewModel.WeatherUiState.Loaded -> WeatherLoadedScreen(state.data)
            else -> {}
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun WeatherLoadedScreen(data: WeatherUiModel) {
    Spacer(modifier = Modifier.size(10.dp))
    Text(
        text = data.city,
        modifier = Modifier.padding(start = 20.dp),
        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
    )
    Text(
        text = todayDate(),
        modifier = Modifier.padding(start = 20.dp),
        style = TextStyle(fontSize = 16.sp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Card(
            modifier = Modifier.size(80.dp).align(Alignment.CenterVertically),
            //backgroundColor = weeklyItemBackgroundColor,
            shape = RoundedCornerShape(40.dp),
            //elevation = 0.dp,
        ) {
            GlideImage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                model = getWeatherIcon(data.todayWeatherIcon[0].description),
                contentScale = ContentScale.Crop,
                contentDescription = stringResource(R.string.image),
            )
        }
        Text(
            text = data.weather,
            modifier = Modifier.padding(start = 10.dp),
            style = TextStyle(
                fontWeight = FontWeight.Bold, fontSize = 70.sp
            )
        )
        Text(
            text = "°C",
            modifier = Modifier.padding(top = 20.dp),
            style = TextStyle(
                fontWeight = FontWeight.Bold, fontSize = 22.sp
            )
        )
        Text(
            text = capitalize(data.todayWeatherIcon[0].description),
            modifier = Modifier
                .padding(start = 20.dp)
                .align(Alignment.CenterVertically),
            style = TextStyle(
                fontWeight = FontWeight.Bold, fontSize = 25.sp
            )
        )
    }
    Spacer(modifier = Modifier.size(15.dp))
    Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DailyItem(
                R.drawable.temprature, data.feelsLike, "Feels Like"
            )
            DailyItem(
                R.drawable.visibility, data.visibility, "Visibility"
            )
            DailyItem(
                R.drawable.uv_rays, data.uvRadiations, "UV Rays"
            )
        }
        Spacer(modifier = Modifier.size(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DailyItem(
                R.drawable.humidity, data.humidity, "Humidity"
            )
            DailyItem(
                R.drawable.wind_speed, data.windSpeed, "Wind Speed"
            )
            DailyItem(
                R.drawable.pressure, data.pressure, "Air Pressure"
            )
        }
    }
    Spacer(modifier = Modifier.size(25.dp))
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp)
    ) {
        items(items = data.forecastForWeek, itemContent = { card ->
            Column {
                Card(
                    modifier = Modifier

                        .height(60.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(CornerSize(15.dp)),


                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = card.day,
                            style = TextStyle(
                                textAlign = TextAlign.Start,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,

                            ),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 15.dp) // Added some padding for separation
                        )

                        GlideImage(
                            modifier = Modifier
                                .weight(1f)
                                .size(40.dp), // Using fixed size for the image
                            model = getWeatherIcon(card.weeklyWeatherIcon[0].description),
                            contentScale = ContentScale.Fit,
                            contentDescription = stringResource(R.string.image),
                        )

                        Row(
                            modifier = Modifier
                                .weight(1f),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = card.dayTemp,
                                style = TextStyle(fontSize = 14.sp),
                            )

                            Text(
                                text = card.nightTemp,
                                style = TextStyle(fontSize = 14.sp),
                                modifier = Modifier.padding(start = 20.dp) // Added some padding for separation
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.size(10.dp))
            }
        })
    }
}

@Composable
fun DailyItem(icDay: Int, data: String, stringText: String) {
    Card(
        modifier = Modifier.size(100.dp),
        shape = RoundedCornerShape(15.dp),

    ) {
        Column(verticalArrangement = Arrangement.Center) {
            Icon(
                painter = painterResource(id = icDay),
                contentDescription = stringResource(R.string.day_temp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(25.dp)
            )
            Spacer(modifier = Modifier.size(5.dp))
            Text(
                text = data,
                style = TextStyle(fontSize = 16.sp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 5.dp)
            )

            Text(
                text = stringText,
                style = TextStyle(fontSize = 14.sp),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}



@Composable
fun WeatherLoadingAnimation() {
    val infiniteTransition = rememberInfiniteTransition()

    // Create an animation state for the scale factor
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1000 // Total duration of one zoom in/out cycle
                0.5f at 500 // Zoom in at the halfway point (1000ms)
            },
            repeatMode = RepeatMode.Reverse
        )
    )

    // Apply the zoom effect to the image
    Image(
        painter = painterResource(id = R.drawable.weather), // Replace with your icon
        contentDescription = null,
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
    )
}











