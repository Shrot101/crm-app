package com.akashicsoft.crm.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akashicsoft.crm.model.Organization
import com.akashicsoft.crm.ui.components.CrmTopAppBar
import com.akashicsoft.crm.util.NumberFormatter
import com.akashicsoft.crm.viewModel.OrgDetailViewModel

// ── Brand colours ─────────────────────────────────────────────────────────────
private val OrgPrimary   = Color(0xFF6200EE)
private val OrgSecondary = Color(0xFF7C4DFF)
private val OrgBg        = Color(0xFFF5F3FF)  // very light purple tint

@Composable
fun OrgDetailScreen(
    viewModel: OrgDetailViewModel,
    orgId: String,
    onNavigateBack: () -> Unit,
    onEditOrg: (String) -> Unit = {}
) {
    val orgState by viewModel.org.collectAsState()
    val scrollState = rememberScrollState()
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(orgId) {
        viewModel.loadOrg(orgId)
    }

    Scaffold(
        topBar = {
            CrmTopAppBar(
                title = "Organisation Details",
                isSubScreen = true,
                onMenuClick = onNavigateBack,
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = { showMenu = false; onEditOrg(orgId) },
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) }
                            )
                        }
                    }
                }
            )
        },
        containerColor = OrgBg
    ) { padding ->
        orgState?.let { org ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── 1. Profile Header ──────────────────────────────────────────
                OrgProfileHeader(org)

                // ── 2. Stats Row ───────────────────────────────────────────────
                OrgStatsRow(org, modifier = Modifier.padding(horizontal = 16.dp))

                // ── 3. Company Information ─────────────────────────────────────
                OrgInfoCard(
                    title = "Company Information",
                    icon  = Icons.Default.Business,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    OrgInfoItem(icon = Icons.Default.Language,     label = "Website",  value = org.website)
                    OrgInfoItem(icon = Icons.Default.Category,     label = "Industry", value = org.industry)
                    OrgInfoItem(icon = Icons.Default.WorkspacePremium, label = "Type",     value = org.type)
                }

                // ── 4. Business Details ────────────────────────────────────────
                OrgInfoCard(
                    title = "Business Details",
                    icon  = Icons.Default.Insights,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    OrgInfoItem(icon = Icons.Default.Source,  label = "Lead Source",        value = org.leadSource)
                    OrgInfoItem(
                        icon  = Icons.Default.AccountTree,
                        label = "Associated Company",
                        value = if (org.associatedCompany.isBlank() || org.associatedCompany == "-")
                            "None" else org.associatedCompany
                    )
                }

                // ── 5. Location ────────────────────────────────────────────────
                OrgInfoCard(
                    title = "Location",
                    icon  = Icons.Default.LocationOn,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    OrgInfoItem(icon = Icons.Default.Home,        label = "Address", value = org.address)
                    OrgInfoItem(icon = Icons.Default.LocationCity, label = "City",    value = org.city)
                    OrgInfoItem(icon = Icons.Default.Map,          label = "State",   value = org.state)
                }

                // ── 6. Ownership & Timeline ────────────────────────────────────
                OrgInfoCard(
                    title = "Ownership & Timeline",
                    icon  = Icons.Default.AssignmentInd,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    OrgInfoItem(icon = Icons.Default.Person,        label = "Assigned To",  value = org.assignedTo)
                    OrgInfoItem(
                        icon  = Icons.Default.CalendarToday,
                        label = "Created Date",
                        value = org.createdAt?.take(10) ?: "Unknown"
                    )
                    OrgInfoItem(
                        icon  = Icons.Default.Tag,
                        label = "Organisation Number",
                        value = org.orgNo
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = OrgPrimary)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Profile Header
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OrgProfileHeader(org: Organization) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(OrgPrimary, OrgSecondary)
                )
            )
            .padding(top = 28.dp, bottom = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Avatar circle with building icon
            Surface(
                modifier = Modifier.size(88.dp),
                shape    = CircleShape,
                color    = Color.White.copy(alpha = 0.2f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text       = org.organizationName.take(1).uppercase(),
                        fontSize   = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Organisation name
            Text(
                text       = org.organizationName,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold,
                color      = Color.White,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(4.dp))

            // Org number
            Text(
                text  = "#${org.orgNo}",
                fontSize = 13.sp,
                color = Color.White.copy(alpha = 0.75f)
            )

            Spacer(Modifier.height(12.dp))

            // Industry + Type badges
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                OrgBadge(text = org.industry, color = Color.White.copy(alpha = 0.2f))
                OrgBadge(text = org.type, color = orgTypeColor(org.type).copy(alpha = 0.85f))
            }
        }
    }
}

@Composable
private fun OrgBadge(text: String, color: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text     = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color    = Color.White
        )
    }
}

private fun orgTypeColor(type: String): Color = when (type.lowercase()) {
    "customer"  -> Color(0xFF4CAF50)
    "partner"   -> Color(0xFF2196F3)
    "prospect"  -> Color(0xFFFF9800)
    else        -> Color(0xFF9E9E9E)
}

// ─────────────────────────────────────────────────────────────────────────────
//  Stats Row
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OrgStatsRow(org: Organization, modifier: Modifier = Modifier) {
    Row(
        modifier  = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OrgStatChip(
            icon  = Icons.Default.People,
            label = "Employees",
            value = org.numberOfEmployees.toString(),
            chipColor  = Color(0xFF6200EE).copy(alpha = 0.08f),
            labelColor = OrgPrimary,
            modifier   = Modifier.weight(1f)
        )
        OrgStatChip(
            icon  = Icons.Default.AttachMoney,
            label = "Target Amount",
            value = "₹ ${NumberFormatter.formatCurrency(org.targetAmount)}",
            chipColor  = Color(0xFF4CAF50).copy(alpha = 0.08f),
            labelColor = Color(0xFF388E3C),
            modifier   = Modifier.weight(1f)
        )
    }
}

@Composable
private fun OrgStatChip(
    icon      : ImageVector,
    label     : String,
    value     : String,
    chipColor : Color,
    labelColor: Color,
    modifier  : Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape    = RoundedCornerShape(14.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = chipColor,
                shape = CircleShape,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = labelColor, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(label, fontSize = 11.sp, color = Color.Gray)
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Info Card + Item
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun OrgInfoCard(
    title    : String,
    icon     : ImageVector,
    modifier : Modifier = Modifier,
    content  : @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Section title row
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = OrgPrimary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text       = title,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color      = Color.Gray
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color    = Color(0xFFEEEEEE)
            )
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                content()
            }
        }
    }
}

@Composable
private fun OrgInfoItem(
    icon  : ImageVector,
    label : String,
    value : String
) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            icon, null,
            tint     = Color(0xFFBDBDBD),
            modifier = Modifier.size(18.dp).padding(top = 2.dp)
        )
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text  = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )
            Text(
                text       = value.ifBlank { "—" },
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color      = Color.Black
            )
        }
    }
}
