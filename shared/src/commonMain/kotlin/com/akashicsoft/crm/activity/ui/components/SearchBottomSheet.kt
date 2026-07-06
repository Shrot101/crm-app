package com.akashicsoft.crm.activity.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.akashicsoft.crm.activity.model.Participant
import com.akashicsoft.crm.activity.model.RelatedRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    onSearch: (String) -> List<Any>,
    onItemsSelected: (List<Any>) -> Unit,
    multiSelect: Boolean = true
) {
    var query by remember { mutableStateOf("") }
    val results = remember(query) { onSearch(query) }
    val sheetState = rememberModalBottomSheetState()
    
    // Internal state for selection
    val selectedItemsMap = remember { mutableStateMapOf<String, Any>() }

    fun getItemKey(item: Any): String {
        return when(item) {
            is Participant -> "${item.type}_${item.id}"
            is RelatedRecord -> "${item.type}_${item.id}"
            else -> item.toString()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(min = 300.dp, max = 650.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                
                if (multiSelect) {
                    IconButton(
                        onClick = { onItemsSelected(selectedItemsMap.values.toList()) },
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color(0xFF6200EE))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Confirm", modifier = Modifier.size(28.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search...") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Search, null) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(results) { item ->
                    val itemKey = getItemKey(item)
                    val isSelected = selectedItemsMap.containsKey(itemKey)
                    
                    Surface(
                        onClick = { 
                            if (multiSelect) {
                                if (isSelected) {
                                    selectedItemsMap.remove(itemKey)
                                } else {
                                    selectedItemsMap[itemKey] = item
                                }
                            } else {
                                onItemsSelected(listOf(item))
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) Color(0xFFF8F9FE) else Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (multiSelect) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = null // Managed by Surface click
                                )
                                Spacer(Modifier.width(12.dp))
                            }
                            
                            val name = when(item) {
                                is Participant -> item.name
                                is RelatedRecord -> item.name
                                else -> ""
                            }
                            Text(name, modifier = Modifier.weight(1f))
                            
                            if (!multiSelect && isSelected) {
                                Icon(Icons.Default.Check, null, tint = Color(0xFF6200EE))
                            }
                        }
                    }
                }
            }
            
            if (multiSelect) {
                Button(
                    onClick = { onItemsSelected(selectedItemsMap.values.toList()) },
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                ) {
                    Text("Done (${selectedItemsMap.size})")
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
