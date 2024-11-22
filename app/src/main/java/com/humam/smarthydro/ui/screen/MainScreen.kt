package com.humam.smarthydro.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humam.smarthydro.R
import com.humam.smarthydro.model.WaterLevel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() {
    CenterAlignedTopAppBar(
        title = { Text(text = "SmartHydro", fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
        )
    )
}

@Composable
fun WaterLevelItem(index: Int, waterLevel: WaterLevel) {
    val isWet = waterLevel.level != "0" && waterLevel.wet != "NO"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(14.dp))
            .padding(25.dp, 25.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                // Determine the image based on wet conditions
                painter = painterResource(if (isWet) R.drawable.water else R.drawable.warning),
                contentDescription = "",
                Modifier
                    .size(45.dp)
            )
            Column {
                Text("Sensor $index", fontWeight = FontWeight.Bold)
                Text("${waterLevel.level} cm")
            }
        }
    }
}

@Composable
fun Banner() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(14.dp)
            )
            .padding(20.dp, 20.dp)
    ) {
        Image(
            painter = painterResource(if (isSystemInDarkTheme()) R.drawable.collab_white else R.drawable.collab),
            contentDescription = "Collaboration Logo iCATS x Telkom University"
        )
    }
}

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = viewModel()
    val waterLevels by viewModel.waterLevels.collectAsState()

    Scaffold(
        topBar = { AppTopBar() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Banner()
            HorizontalDivider()
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(waterLevels) { index, waterLevel ->
                    WaterLevelItem(index + 1, waterLevel)
                }
            }
        }
    }
}