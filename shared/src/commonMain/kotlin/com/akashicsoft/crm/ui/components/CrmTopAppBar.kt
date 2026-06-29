package com.akashicsoft.crm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrmTopAppBar(
    title: String,
    isSubScreen: Boolean = false,
    navigationIcon: @Composable (() -> Unit)? = null,
    hasActiveFilters: Boolean = false, // New parameter for red dot
    filterBadgeColor: Color = Color.Red, // Parameter for dot color
    actions: @Composable (RowScope.() -> Unit)? = null,
    onMenuClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onFilterClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        },
        navigationIcon = {
            if (navigationIcon != null) {
                navigationIcon()
            } else if (isSubScreen) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            } else {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }
            }
        },
        actions = {
            if (actions != null) {
                actions()
            } else {
                IconButton(onClick = onCalendarClick) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar", tint = Color.White)
                }
                
                // Creative Filter Icon with Red/Orange Dot Badge
                Box(contentAlignment = Alignment.TopStart) {
                    IconButton(onClick = onFilterClick) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter", tint = Color.White)
                    }
                    if (hasActiveFilters) {
                        Box(
                            modifier = Modifier
                                .offset(x = 8.dp, y = 8.dp)
                                .size(8.dp)
                                .background(filterBadgeColor, CircleShape)
                        )
                    }
                }

                IconButton(onClick = onRefreshClick) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF6200EE)
        )
    )
}
