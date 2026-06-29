package com.akashicsoft.crm.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.akashicsoft.crm.data.local.DealUIHelpers
import com.akashicsoft.crm.model.DealListItem
import com.akashicsoft.crm.ui.components.LeadListSkeleton
import com.akashicsoft.crm.viewModel.DealViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealScreen(
    viewModel: DealViewModel,
    modifier: Modifier = Modifier,
    onAddDealClick: () -> Unit = {},
    onEditDeal: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var expandedDealId by remember { mutableStateOf<String?>(null) }
    val backgroundGray = Color(0xFFF8F9FE)

    LaunchedEffect(Unit) {
        viewModel.loadDeads()
    }

    Box(modifier = modifier.fillMaxSize().background(backgroundGray)) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header (Reusing logic for consistency)
            DealHeader(viewModel)

            // Content
            if (state.isLoading) {
                LeadListSkeleton(modifier = Modifier.weight(1f))
            } else if (state.deals.isEmpty()) {
                EmptyDealsView()
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp, 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = state.deals,
                        key = { it._id }
                    ) { deal ->
                        DealCard(
                            deal = deal,
                            isExpanded = expandedDealId == deal._id,
                            onExpandToggle = {
                                expandedDealId = if (expandedDealId == deal._id) null else deal._id
                            },
                            onEdit = { onEditDeal(deal._id) }
                        )
                    }
                }
            }
        }

        // FAB for creating a new Deal
        FloatingActionButton(
            onClick = onAddDealClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF6200EE),
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Default.Add, "Add Deal")
        }
    }
}

@Composable
private fun DealHeader(viewModel: DealViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val (currentPage, totalPages, totalCount) = viewModel.getCurrentPageInfo()
            Column {
                Text(
                    text = "Total: $totalCount deals",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Page $currentPage of $totalPages",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Standard Pagination Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PaginationIconButton(onClick = {}, enabled = false, icon = Icons.Default.ArrowBack)
                PaginationIconButton(onClick = {}, enabled = false, icon = Icons.Default.KeyboardArrowLeft)
                PaginationIconButton(onClick = {}, enabled = false, icon = Icons.Default.KeyboardArrowRight)
                PaginationIconButton(onClick = {}, enabled = false, icon = Icons.Default.ArrowForward)
            }
        }
    }
}

@Composable
fun DealCard(
    deal: DealListItem,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onExpandToggle)
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Row 1: Title and Value
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = deal.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f).padding(end = 16.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$${deal.dealValue ?: 0.0}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF27AE60)
                )
            }

            Spacer(Modifier.height(8.dp))

            // Row 2: Organization and Stage Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Business, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = deal.organization ?: "Individual",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Stage Badge
                val stageColor = DealUIHelpers.getStageColor(deal.stage)
                Surface(
                    color = stageColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(stageColor, CircleShape)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = deal.stage ?: "NEW",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = stageColor
                        )
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF0F0F0))

            // Row 3: Owner and Close Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(28.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = deal.owner.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Medium
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = deal.closeDate ?: "N/A",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // Expanded Content
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Divider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(bottom = 16.dp))
                    
                    // Product and Rating
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Product", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(
                                text = deal.product ?: "General",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Rating", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            val ratingColor = DealUIHelpers.getRatingColor(deal.rating)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, null, tint = ratingColor, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = deal.rating ?: "Normal",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ratingColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Tags
                    if (!deal.tags.isNullOrEmpty()) {
                        Text("Tags", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Spacer(Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(deal.tags) { tag ->
                                Surface(
                                    color = Color(0xFF6200EE).copy(alpha = 0.05f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "#$tag",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF6200EE)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    // Assigned To and Edit Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Assigned To", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                            Text(
                                text = deal.assignedTo?.name ?: "Unassigned",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Deal",
                                tint = Color(0xFF6200EE),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PaginationIconButton(
    onClick: () -> Unit,
    enabled: Boolean,
    icon: ImageVector
) {
    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(32.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (enabled) Color(0xFF6200EE) else Color.LightGray,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun EmptyDealsView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Handshake, null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
            Spacer(Modifier.height(16.dp))
            Text("No deals found", style = MaterialTheme.typography.titleMedium, color = Color.Gray)
        }
    }
}

