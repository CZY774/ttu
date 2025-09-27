package com.czy.ttu.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.czy.ttu.ml.ModelManager
import com.czy.ttu.ui.theme.FruitGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelSwitcher(
    modelManager: ModelManager,
    onModelChanged: (ModelManager.ModelType) -> Unit
) {
    var selectedModel by remember { mutableStateOf(ModelManager.ModelType.QUANTIZED) }
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) { // The 'this' here is ExposedDropdownMenuBoxScope
        OutlinedTextField(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true)
                .fillMaxWidth(),
            readOnly = true,
            value = selectedModel.name,
            onValueChange = {},
            label = { Text("Model Type") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            ModelManager.ModelType.values().forEach { modelType ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(modelType.name)
                            Text(
                                text = modelManager.getModelInfo(),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        selectedModel = modelType
                        onModelChanged(modelType)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
