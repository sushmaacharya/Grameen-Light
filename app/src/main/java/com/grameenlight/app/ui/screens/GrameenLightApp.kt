package com.grameenlight.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.grameenlight.app.data.local.ComplaintEntity
import com.grameenlight.app.data.local.PoleEntity
import com.grameenlight.app.data.local.RepairUpdateEntity
import com.grameenlight.app.data.model.PoleStatus
import com.grameenlight.app.data.model.ReportStatus
import com.grameenlight.app.data.model.SyncStatus
import com.grameenlight.app.data.model.ThemeMode
import com.grameenlight.app.data.model.TrackerFilter
import com.grameenlight.app.data.model.TrackerStatus
import com.grameenlight.app.ui.theme.AssignedBlue
import com.grameenlight.app.ui.theme.DayBurningAmber
import com.grameenlight.app.ui.theme.FusedRed
import com.grameenlight.app.ui.theme.UnknownGrey
import com.grameenlight.app.ui.theme.WorkingGreen
import com.grameenlight.app.ui.viewmodel.EnergyViewModel
import com.grameenlight.app.ui.viewmodel.PanchayatViewModel
import com.grameenlight.app.ui.viewmodel.PoleViewModel
import com.grameenlight.app.ui.viewmodel.ReportViewModel
import com.grameenlight.app.ui.viewmodel.SettingsViewModel
import com.grameenlight.app.ui.viewmodel.SyncSettingsState
import com.grameenlight.app.ui.viewmodel.TrackerCard
import com.grameenlight.app.ui.viewmodel.TrackerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

private val PanelShape = RoundedCornerShape(18.dp)
private val ChipShape = RoundedCornerShape(12.dp)
private val LightHeroGreen = Color(0xFF1F6A37)
private val LightMetricGreen = Color(0xFF1C6A28)
private val LightInfoBlue = Color(0xFFDCE9F9)
private val LightNotice = Color(0xFFFFF1CF)
private val DarkHeroGreen = Color(0xFF285C39)
private val DarkMetricGreen = Color(0xFF1E5A2E)
private val DarkInfoBlue = Color(0xFF203E63)
private val DarkNotice = Color(0xFF3A3221)

enum class AppSection(
    val label: String,
    val shortLabel: String,
    val title: String,
    val subtitle: String
) {
    POLES("Pole Map", "Map", "Pole map", "Ward wise streetlight view"),
    TRACKER("Tracker", "Track", "Repair tracker", "Complaints and repair progress"),
    ENERGY("Energy", "kWh", "Energy saved", "Monthly village impact"),
    PANCHAYAT("Panchayat", "PIN", "Panchayat mode", "Admin access and actions"),
    SETTINGS("Settings", "Set", "Settings", "Theme and app status")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrameenLightApp(
    poleViewModel: PoleViewModel,
    reportViewModel: ReportViewModel,
    trackerViewModel: TrackerViewModel,
    energyViewModel: EnergyViewModel,
    panchayatViewModel: PanchayatViewModel,
    settingsViewModel: SettingsViewModel
) {
    var selectedSection by remember { mutableStateOf(AppSection.POLES) }
    var reportPole by remember { mutableStateOf<PoleEntity?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val poleState by poleViewModel.uiState.collectAsState()
    val reportState by reportViewModel.uiState.collectAsState()
    val trackerState by trackerViewModel.uiState.collectAsState()
    val energySummary by energyViewModel.summary.collectAsState()
    val panchayatState by panchayatViewModel.uiState.collectAsState()
    val themeMode by settingsViewModel.themeMode.collectAsState()
    val settingsSyncState by settingsViewModel.syncState.collectAsState()

    LaunchedEffect(reportState.lastResult, reportState.errorMessage) {
        reportState.lastResult?.let {
            val syncText = when (it.syncStatus) {
                SyncStatus.SYNCED -> "synced"
                SyncStatus.PENDING -> "saved locally, syncing in background"
                SyncStatus.FAILED -> "saved locally, but cloud sync failed"
            }
            snackbarHostState.showSnackbar("Complaint ${it.complaintId} $syncText")
            reportViewModel.clearMessage()
        }
        reportState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            reportViewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            ScreenTopBar(
                section = selectedSection,
                themeMode = themeMode
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            VillageBottomBar(
                selectedSection = selectedSection,
                onSectionSelected = { selectedSection = it }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            when (selectedSection) {
                AppSection.POLES -> PoleMapScreen(
                    poles = poleState.poles,
                    selectedPole = poleState.selectedPole,
                    onPoleClick = poleViewModel::selectPole,
                    onReportClick = { reportPole = it }
                )

                AppSection.TRACKER -> TrackerScreen(
                    complaints = trackerState.filteredComplaints,
                    selectedFilter = trackerState.filter,
                    poleQuery = trackerState.poleQuery,
                    firebaseReady = panchayatState.isFirebaseConfigured,
                    onFilterChange = trackerViewModel::setFilter,
                    onPoleQueryChange = trackerViewModel::setPoleQuery
                )

                AppSection.ENERGY -> EnergyScreen(
                    kwhSaved = energySummary.kwhSaved,
                    inrSaved = energySummary.inrSaved,
                    fixedCount = energySummary.fixedDayBurningCount
                )

                AppSection.PANCHAYAT -> PanchayatScreen(
                    isUnlocked = panchayatState.isUnlocked,
                    pinError = panchayatState.pinError,
                    pinMessage = panchayatState.pinMessage,
                    complaints = panchayatState.complaints,
                    summary = panchayatState.prioritySummary,
                    firebaseReady = panchayatState.isFirebaseConfigured,
                    onUnlock = panchayatViewModel::unlock,
                    onChangePin = panchayatViewModel::changePin,
                    onClearPinMessage = panchayatViewModel::clearPinMessage,
                    onUpdateStatus = panchayatViewModel::updateStatus,
                    onSummarize = panchayatViewModel::summarizeToday
                )

                AppSection.SETTINGS -> SettingsScreen(
                    themeMode = themeMode,
                    syncState = settingsSyncState,
                    onToggleTheme = settingsViewModel::toggleTheme
                )
            }
        }
    }

    reportPole?.let { pole ->
        QuickReportDialog(
            pole = pole,
            isSaving = reportState.isSaving,
            onDismiss = { reportPole = null },
            onSubmit = { status, note ->
                reportViewModel.submitReport(pole.poleId, status, note)
                reportPole = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenTopBar(
    section: AppSection,
    themeMode: ThemeMode
) {
    TopAppBar(
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(section.title, fontWeight = FontWeight.Bold)
                Text(
                    if (section == AppSection.SETTINGS) themeMode.label else section.subtitle,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
private fun VillageBottomBar(
    selectedSection: AppSection,
    onSectionSelected: (AppSection) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.primary,
        tonalElevation = 0.dp
    ) {
        AppSection.entries.forEach { section ->
            NavigationBarItem(
                selected = selectedSection == section,
                onClick = { onSectionSelected(section) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimary,
                    unselectedIconColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f),
                    unselectedTextColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f),
                    indicatorColor = Color.Transparent
                ),
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(if (selectedSection == section) 22.dp else 14.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(50))
                                .background(
                                    if (selectedSection == section) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.48f)
                                    }
                                )
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(section.shortLabel, style = MaterialTheme.typography.labelSmall)
                    }
                },
                label = null
            )
        }
    }
}

@Composable
private fun PoleMapScreen(
    poles: List<PoleEntity>,
    selectedPole: PoleEntity?,
    onPoleClick: (String) -> Unit,
    onReportClick: (PoleEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenSectionLabel("All poles")
        }
        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(388.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(poles, key = { it.poleId }) { pole ->
                    PoleNode(
                        pole = pole,
                        selected = selectedPole?.poleId == pole.poleId,
                        onClick = { onPoleClick(pole.poleId) }
                    )
                }
            }
        }
        item {
            LegendRow()
        }
        item {
            selectedPole?.let { pole ->
                DarkPanelCard {
                    Text(
                        "${pole.poleId} - Ward ${pole.wardNo}",
                        color = strongPanelTextColor(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        pole.streetName,
                        color = panelMutedText(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SoftStatusPill(pole.currentStatus.label, pole.currentStatus.statusColor())
                        Text(
                            formatDateTime(pole.lastUpdatedAt),
                            color = panelMutedText(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    Button(
                        onClick = { onReportClick(pole) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Quick report")
                    }
                }
            } ?: SoftPanelCard {
                Text(
                    "Select a pole to view details and report a status.",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PoleNode(
    pole: PoleEntity,
    selected: Boolean,
    onClick: () -> Unit
) {
    val statusColor = pole.currentStatus.statusColor()
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = statusColor.copy(alpha = if (selected) 0.28f else 0.18f)
        ),
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) statusColor else statusColor.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                pole.poleId.removePrefix("GL-"),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LegendRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LegendChip("Working", WorkingGreen)
        LegendChip("Burning", DayBurningAmber)
        LegendChip("Assigned", AssignedBlue)
        LegendChip("Unknown", UnknownGrey)
        LegendChip("Fused", FusedRed)
    }
}

@Composable
private fun LegendChip(label: String, color: Color) {
    AssistChip(
        onClick = {},
        label = { Text(label) },
        leadingIcon = {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.14f),
            labelColor = MaterialTheme.colorScheme.onSurface,
            leadingIconContentColor = color
        )
    )
}

@Composable
private fun QuickReportDialog(
    pole: PoleEntity,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (ReportStatus, String?) -> Unit
) {
    var note by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = panelBackground(),
        shape = RoundedCornerShape(24.dp),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("Quick report", fontWeight = FontWeight.Bold)
                Text("${pole.poleId} - ${pole.streetName}", style = MaterialTheme.typography.bodyMedium)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                DarkPanelCard {
                    Text(
                        "${pole.poleId} - Ward ${pole.wardNo}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Last status: ${pole.currentStatus.label}",
                        color = panelMutedText(),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "Updated: ${formatDateTime(pole.lastUpdatedAt)}",
                        color = panelMutedText(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                ScreenSectionLabel("Choose status")
                ReportStatus.entries.forEach { status ->
                    Button(
                        onClick = { onSubmit(status, note) },
                        enabled = !isSaving,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = status.statusColor().copy(alpha = 0.14f),
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        border = BorderStroke(1.dp, status.statusColor().copy(alpha = 0.55f))
                    ) {
                        Text(status.label)
                    }
                }
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it.take(150) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Optional note") },
                    shape = RoundedCornerShape(14.dp),
                    colors = villageTextFieldColors(),
                    maxLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
private fun TrackerScreen(
    complaints: List<TrackerCard>,
    selectedFilter: TrackerFilter,
    poleQuery: String,
    firebaseReady: Boolean,
    onFilterChange: (TrackerFilter) -> Unit,
    onPoleQueryChange: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenSectionLabel("Filters")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TrackerFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { onFilterChange(filter) },
                        label = { Text(filter.label) },
                        shape = ChipShape,
                        colors = androidx.compose.material3.FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                            labelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
            if (selectedFilter == TrackerFilter.POLE_ID) {
                OutlinedTextField(
                    value = poleQuery,
                    onValueChange = onPoleQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    label = { Text("Search pole ID") },
                    shape = RoundedCornerShape(14.dp),
                    colors = villageTextFieldColors()
                )
            }
        }
        item {
            SoftPanelCard {
                StatusLine(
                    "Realtime sync",
                    if (firebaseReady) "Connected" else "Local only"
                )
            }
        }
        if (complaints.isEmpty()) {
            item { EmptyPanel("No complaints match this filter.") }
        } else {
            items(complaints, key = { it.complaint.complaintId }) { trackerCard ->
                ComplaintCard(
                    complaint = trackerCard.complaint,
                    history = trackerCard.history,
                    firebaseReady = firebaseReady
                )
            }
        }
    }
}

@Composable
private fun PanchayatScreen(
    isUnlocked: Boolean,
    pinError: String?,
    pinMessage: String?,
    complaints: List<ComplaintEntity>,
    summary: String,
    firebaseReady: Boolean,
    onUnlock: (String) -> Unit,
    onChangePin: (String, String, String) -> Unit,
    onClearPinMessage: () -> Unit,
    onUpdateStatus: (String, TrackerStatus) -> Unit,
    onSummarize: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var currentPin by remember { mutableStateOf("") }
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }

    LaunchedEffect(pinMessage) {
        if (pinMessage != null) {
            currentPin = ""
            newPin = ""
            confirmPin = ""
            delay(2200)
            onClearPinMessage()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (!isUnlocked) {
            item {
                SoftPanelCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ScreenSectionLabel("Demo PIN")
                        PinPreview(pin)
                        OutlinedTextField(
                            value = pin,
                            onValueChange = { pin = it.filter(Char::isDigit).take(4) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Enter Panchayat PIN") },
                            shape = RoundedCornerShape(14.dp),
                            colors = villageTextFieldColors(),
                            isError = pinError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                        )
                        pinError?.let { ErrorText(it) }
                        Button(
                            onClick = { onUnlock(pin) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Unlock")
                        }
                    }
                }
            }
        } else {
            item {
                SummaryPanel(
                    title = "GenAI summary",
                    summary = summary,
                    accentColor = infoBannerColor(),
                    footer = if (firebaseReady) "Verified sync active" else "Offline fallback active",
                    actionLabel = "Summarise today",
                    onAction = onSummarize
                )
            }
            item {
                SoftPanelCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Change Panchayat PIN", fontWeight = FontWeight.Bold)
                        OutlinedTextField(
                            value = currentPin,
                            onValueChange = { currentPin = it.filter(Char::isDigit).take(4) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Current PIN") },
                            shape = RoundedCornerShape(14.dp),
                            colors = villageTextFieldColors(),
                            isError = pinError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                        )
                        OutlinedTextField(
                            value = newPin,
                            onValueChange = { newPin = it.filter(Char::isDigit).take(4) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("New PIN") },
                            shape = RoundedCornerShape(14.dp),
                            colors = villageTextFieldColors(),
                            isError = pinError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                        )
                        OutlinedTextField(
                            value = confirmPin,
                            onValueChange = { confirmPin = it.filter(Char::isDigit).take(4) },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Confirm new PIN") },
                            shape = RoundedCornerShape(14.dp),
                            colors = villageTextFieldColors(),
                            isError = pinError != null,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                        )
                        pinError?.let { ErrorText(it) }
                        pinMessage?.let {
                            Text(
                                it,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Button(
                            onClick = { onChangePin(currentPin, newPin, confirmPin) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Text("Save new PIN")
                        }
                    }
                }
            }
            if (complaints.isEmpty()) {
                item { EmptyPanel("No open repair complaints.") }
            } else {
                items(complaints, key = { it.complaintId }) { complaint ->
                    ComplaintCard(
                        complaint = complaint,
                        history = emptyList(),
                        firebaseReady = firebaseReady,
                        actions = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CompactActionButton(
                                    label = "Assign",
                                    background = AssignedBlue.copy(alpha = 0.2f),
                                    foreground = AssignedBlue,
                                    onClick = { onUpdateStatus(complaint.complaintId, TrackerStatus.Assigned) }
                                )
                                CompactActionButton(
                                    label = "Mark fixed",
                                    background = WorkingGreen.copy(alpha = 0.2f),
                                    foreground = WorkingGreen,
                                    onClick = { onUpdateStatus(complaint.complaintId, TrackerStatus.Fixed) }
                                )
                                CompactActionButton(
                                    label = "Close",
                                    background = UnknownGrey.copy(alpha = 0.18f),
                                    foreground = MaterialTheme.colorScheme.onSurface,
                                    onClick = { onUpdateStatus(complaint.complaintId, TrackerStatus.Closed) }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EnergyScreen(
    kwhSaved: Double,
    inrSaved: Double,
    fixedCount: Int
) {
    val savedHours = fixedCount * 5
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Energy saved", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("May 2026", color = sectionMutedColor(), style = MaterialTheme.typography.bodyMedium)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.14f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        "+${fixedCount} this week",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        item {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = PanelShape,
                colors = CardDefaults.elevatedCardColors(containerColor = energyHeroColor())
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 22.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "ENERGY SAVED THIS MONTH",
                        color = Color.White.copy(alpha = 0.84f),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        "${"%.2f".format(kwhSaved)} kWh",
                        color = Color.White,
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "~ INR ${"%.2f".format(inrSaved)} saved",
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        EnergyMetric("Fixed", fixedCount.toString())
                        EnergyMetric("Avg lamp", "40W")
                        EnergyMetric("Saved", "${savedHours}h")
                    }
                }
            }
        }
        item {
            ScreenSectionLabel("Resolved issues")
        }
        if (fixedCount == 0) {
            item { EmptyPanel("No fixed daytime-burning cases yet.") }
        } else {
            items(fixedCount) { index ->
                SoftPanelCard {
                    Text(
                        "GL-202605-${(index + 1).toString().padStart(2, '0')} - P${(index + 9).toString().padStart(2, '0')}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        "40W - ${(index + 6)} hrs saved - ${"%.2f".format((index + 6) * 0.04)} kWh",
                        color = sectionMutedColor(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        item {
            ElevatedCard(
                shape = PanelShape,
                colors = CardDefaults.elevatedCardColors(containerColor = noticePanelColor())
            ) {
                Text(
                    "Report lamps burning during the day to save more energy next month.",
                    modifier = Modifier.padding(16.dp),
                    color = noticeTextColor(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    themeMode: ThemeMode,
    syncState: SyncSettingsState,
    onToggleTheme: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ScreenSectionLabel("Theme")
            ModeToggleCard(
                title = "Night audit mode",
                checked = themeMode == ThemeMode.NIGHT,
                onCheckedChange = { if (themeMode != ThemeMode.NIGHT) onToggleTheme() }
            )
            Spacer(Modifier.height(10.dp))
            ModeToggleCard(
                title = "Day audit mode",
                checked = themeMode == ThemeMode.DAY,
                onCheckedChange = { if (themeMode != ThemeMode.DAY) onToggleTheme() }
            )
        }
        item {
            ScreenSectionLabel("Sync status")
            DarkPanelCard {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatusBulletRow("Firebase connected", syncState.syncStatus == SyncStatus.SYNCED)
                    StatusBulletRow(
                        when (syncState.syncStatus) {
                            SyncStatus.PENDING -> "Records pending"
                            SyncStatus.FAILED -> "Sync attention needed"
                            SyncStatus.SYNCED -> "All visible records synced"
                        },
                        true
                    )
                    syncState.lastSyncTime?.let {
                        StatusBulletRow("Last sync: ${formatTimeOnly(it)}", true)
                    }
                }
            }
        }
        item {
            ScreenSectionLabel("App")
            DarkPanelCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Grameen-Light v1.0", color = strongPanelTextColor(), fontWeight = FontWeight.Bold)
                    Text(
                        "Room DB (Offline storage)",
                        color = strongPanelSecondaryTextColor(),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "Firebase (Real-time sync): ${syncState.syncStatus.label}",
                        color = strongPanelSecondaryTextColor(),
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        "Panchayat PIN: Managed in Panchayat Mode",
                        color = strongPanelSecondaryTextColor(),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ComplaintCard(
    complaint: ComplaintEntity,
    history: List<RepairUpdateEntity>,
    firebaseReady: Boolean = true,
    actions: @Composable (() -> Unit)? = null
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = PanelShape,
        colors = CardDefaults.elevatedCardColors(containerColor = strongPanelBackground())
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .height(116.dp)
                    .clip(RoundedCornerShape(50))
                    .background(complaint.selectedStatus.statusColor())
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        complaint.complaintId,
                        color = complaint.selectedStatus.statusColor(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    SoftStatusPill(complaint.trackerStatus.label, complaint.trackerStatus.statusColor())
                }
                Text(
                    "${complaint.poleId} - ${complaint.streetName}",
                    color = strongPanelTextColor(),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                complaint.note?.takeIf { it.isNotBlank() }?.let {
                    Text(it, color = strongPanelSecondaryTextColor(), style = MaterialTheme.typography.bodySmall)
                }
                Text(
                    "Updated ${formatDateTime(complaint.updatedAt)}",
                    color = strongPanelTertiaryTextColor(),
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    "Sync: ${complaint.syncStatus.displayLabel(firebaseReady)}",
                    color = strongPanelSecondaryTextColor(),
                    style = MaterialTheme.typography.labelSmall
                )
                if (history.isNotEmpty()) {
                    HorizontalDivider(color = strongPanelDividerColor())
                    history.take(3).forEach { update ->
                        Text(
                            "${update.oldStatus.label} -> ${update.newStatus.label}",
                            color = strongPanelSecondaryTextColor(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                actions?.invoke()
            }
        }
    }
}

@Composable
private fun SummaryPanel(
    title: String,
    summary: String,
    accentColor: Color,
    footer: String,
    actionLabel: String,
    onAction: () -> Unit
) {
    val summaryLines = remember(summary) {
        summary.lines().map { it.trim() }.filter { it.isNotBlank() }
    }
    SoftPanelCard {
        Text(title, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
        ElevatedCard(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = accentColor)
        ) {
            Text(
                "AI-generated - for triage only - not official",
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                color = infoBannerTextColor(),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        ElevatedCard(
            modifier = Modifier.border(BorderStroke(1.dp, panelBorderColor()), RoundedCornerShape(18.dp)),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = panelBackground())
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                summaryLines.forEachIndexed { index, line ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 6.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (index == summaryLines.lastIndex) DayBurningAmber else MaterialTheme.colorScheme.primary)
                            )
                            Text(
                                line.removePrefix("-").trim(),
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        if (index != summaryLines.lastIndex) {
                            HorizontalDivider(color = panelBorderColor())
                        }
                }
            }
        }
        Button(
            onClick = onAction,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(actionLabel)
        }
        ElevatedCard(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = panelBackground())
        ) {
            Text(
                footer,
                modifier = Modifier.padding(14.dp),
                color = sectionMutedColor(),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun SoftPanelCard(content: @Composable ColumnScope.() -> Unit) {
    ElevatedCard(
        modifier = Modifier.border(BorderStroke(1.dp, panelBorderColor()), PanelShape),
        shape = PanelShape,
        colors = CardDefaults.elevatedCardColors(containerColor = panelBackground())
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
private fun DarkPanelCard(content: @Composable ColumnScope.() -> Unit) {
    ElevatedCard(
        modifier = Modifier.border(BorderStroke(1.dp, strongPanelBorderColor()), PanelShape),
        shape = PanelShape,
        colors = CardDefaults.elevatedCardColors(containerColor = strongPanelBackground())
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
private fun EmptyPanel(message: String) {
    SoftPanelCard {
        Text(
            message,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
private fun ScreenSectionLabel(text: String) {
    Text(
        text.uppercase(),
        color = sectionMutedColor(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold
    )
}

@Composable
private fun SoftStatusPill(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.18f))
            .border(BorderStroke(1.dp, color.copy(alpha = 0.45f)), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            label,
            color = if (color == DayBurningAmber) Color(0xFF7A5300) else color,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun CompactActionButton(
    label: String,
    background: Color,
    foreground: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = foreground
        )
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun PinPreview(pin: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(4) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(strongPanelBackground())
                    .border(BorderStroke(1.dp, strongPanelBorderColor()), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (index < pin.length) "*" else "",
                    color = strongPanelTextColor(),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun EnergyMetric(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            value,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            color = Color.White.copy(alpha = 0.78f),
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun ModeToggleCard(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ElevatedCard(
        shape = PanelShape,
        colors = CardDefaults.elevatedCardColors(containerColor = strongPanelBackground())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, color = strongPanelTextColor(), fontWeight = FontWeight.SemiBold)
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun StatusBulletRow(label: String, active: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(if (active) WorkingGreen else strongPanelSecondaryTextColor().copy(alpha = 0.42f))
        )
        Text(
            label,
            color = strongPanelSecondaryTextColor(),
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ErrorText(message: String) {
    Text(
        message,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun StatusLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
        Text(value, textAlign = TextAlign.End)
    }
}

@Composable
private fun villageTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = panelBackground(),
    unfocusedContainerColor = panelBackground(),
    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
    unfocusedBorderColor = panelBorderColor(),
    cursorColor = MaterialTheme.colorScheme.primary
)

@Composable
private fun isNightPalette(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f

@Composable
private fun panelBackground(): Color =
    if (isNightPalette()) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface

@Composable
private fun strongPanelBackground(): Color =
    if (isNightPalette()) MaterialTheme.colorScheme.surfaceVariant else Color(0xFFF1F5EE)

@Composable
private fun strongPanelTextColor(): Color =
    if (isNightPalette()) Color.White else MaterialTheme.colorScheme.onSurface

@Composable
private fun strongPanelSecondaryTextColor(): Color =
    if (isNightPalette()) Color(0xFFD8D4CC) else Color(0xFF586258)

@Composable
private fun strongPanelTertiaryTextColor(): Color =
    if (isNightPalette()) Color(0xFFBBB7AF) else Color(0xFF717A71)

@Composable
private fun strongPanelDividerColor(): Color =
    if (isNightPalette()) Color(0x33FFFFFF) else Color(0x16000000)

@Composable
private fun strongPanelBorderColor(): Color =
    if (isNightPalette()) Color(0xFF334137) else Color(0xFFD6DED4)

@Composable
private fun panelBorderColor(): Color =
    if (isNightPalette()) Color(0xFF334137) else Color(0xFFDCE4DA)

@Composable
private fun panelMutedText(): Color =
    if (isNightPalette()) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF667165)

@Composable
private fun sectionMutedColor(): Color =
    if (isNightPalette()) MaterialTheme.colorScheme.onSurfaceVariant else Color(0xFF778275)

@Composable
private fun infoBannerColor(): Color = if (isNightPalette()) DarkInfoBlue else LightInfoBlue

@Composable
private fun infoBannerTextColor(): Color = if (isNightPalette()) Color(0xFFE7F0FD) else Color(0xFF2C5A8F)

@Composable
private fun noticePanelColor(): Color = if (isNightPalette()) DarkNotice else LightNotice

@Composable
private fun noticeTextColor(): Color = if (isNightPalette()) Color(0xFFF4DA98) else Color(0xFF8A5B00)

@Composable
private fun energyHeroColor(): Color = if (isNightPalette()) DarkMetricGreen else LightMetricGreen

private fun PoleStatus.statusColor(): Color = when (this) {
    PoleStatus.Working -> WorkingGreen
    PoleStatus.Fused -> FusedRed
    PoleStatus.BurningInDay -> DayBurningAmber
    PoleStatus.Assigned -> AssignedBlue
    PoleStatus.Unknown -> UnknownGrey
}

private fun ReportStatus.statusColor(): Color = when (this) {
    ReportStatus.Working -> WorkingGreen
    ReportStatus.Fused -> FusedRed
    ReportStatus.BurningInDay -> DayBurningAmber
}

private fun TrackerStatus.statusColor(): Color = when (this) {
    TrackerStatus.Reported -> Color(0xFFF2C66D)
    TrackerStatus.Assigned -> AssignedBlue
    TrackerStatus.Fixed -> WorkingGreen
    TrackerStatus.Closed -> Color(0xFFE0DED8)
}

private fun SyncStatus.displayLabel(firebaseReady: Boolean): String = when {
    this == SyncStatus.PENDING && !firebaseReady -> "Saved locally only"
    this == SyncStatus.PENDING -> "Pending cloud sync"
    this == SyncStatus.FAILED -> "Cloud sync failed"
    else -> label
}

private fun formatDateTime(timestamp: Long): String {
    return SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(timestamp))
}

private fun formatTimeOnly(timestamp: Long): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
}

