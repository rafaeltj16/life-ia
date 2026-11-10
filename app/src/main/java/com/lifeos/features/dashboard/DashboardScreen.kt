package com.lifeos.features.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lifeos.widgets.*

@Composable
fun LifeOSDashboardScreen(
    onNavigateTo: (String) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ExecutiveSummaryWidget()
        LifeAIPipelineWidget()
        ProjectWorkflowWidget(onStartFocusSession = { onNavigateTo("focus") })
        SmartTimelineWidget()
        FocusAnalyticsWidget()
        LifeGraphEngineWidget()
        FinancialWidget()
        HealthWidget()
        KnowledgeWidget()
    }
}
