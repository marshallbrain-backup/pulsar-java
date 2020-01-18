package ui.colony.construction

import com.marshalldbrain.pulsar.colony.Colony
import com.marshalldbrain.pulsar.colony.districts.District
import com.marshalldbrain.pulsar.colony.districts.DistrictType
import ui.util.swing.getSelected
import ui.util.swing.initGridBagConstraints
import java.awt.GridBagConstraints
import java.awt.event.ActionEvent
import javax.swing.ButtonGroup
import javax.swing.JLabel
import javax.swing.JRadioButton
import javax.swing.table.DefaultTableModel

object DistrictUi : ConstructionUi {
	
	private val buttonList = listOf(
		JRadioButton("Build"),
		JRadioButton("Destroy"),
		JRadioButton("Replace"),
		JRadioButton("Tool"),
		JRadioButton("Detool"),
		JRadioButton("Upgrade")
	)
	private val buttonGroup = ButtonGroup()
	
	init {
		
		buttonList.forEach { b ->
			buttonGroup.add(b)
			b.name = b.text.toLowerCase()
		}
		
		buttonList[5].isEnabled = false
		
	}
	
	private fun radioButtonListener(e: ActionEvent, colony: Colony) {
		
		val slot = ConstructionUiElements.slot
		slot.removeAllItems()
		
		when((e.source as JRadioButton).name) {
			"build" -> {
				colony.districts.filter { it.type.isTooled }
			}
			"destroy" -> {
				colony.districts.filter { it.type.isTooled && it.amount > 0 }
			}
			"replace" -> {
				colony.districts.filter { it.type.isTooled && it.amount > 0 }
			}
			"tool" -> {
				colony.districts.distinctBy { it.type }
			}
			"detool" -> {
				colony.districts.filter { it.type.isTooled }
			}
			"upgrade" -> {
				emptyList()
			}
			else -> emptyList()
		}.forEach { slot.addItem(it) }
	}
	
	override fun activate(colony: Colony) {
		
		buttonList.forEach { b ->
			b.actionListeners.forEach { b.removeActionListener(it) }
			b.addActionListener { radioButtonListener(it, colony) }
		}
		
		val slot = ConstructionUiElements.slot
		
		println("District Ui activated")
		ConstructionUiElements.slot.isVisible = true
		
//		val items = mutableListOf<District>()
//		for (d in colony.districts) {
//			if (d.type.isTooled) {
//				items.add(d)
//			}
//		}
		
//		slot.addItem("Untooled District (${colony.districts.size - items.size})")
//		val untooledSlots = colony.untooledDistricts
//		if (untooledSlots.isNotEmpty()) {
//			slot.addItem(untooledSlots)
//		}
		
		val panel = ConstructionUiElements.projectCreatePanel
		val c = GridBagConstraints()
		initGridBagConstraints(c)
		
		buttonList.forEach {
			c.gridx++
			panel.add(it, c)
		}
		
		c.gridx++
		c.weightx = 1.0
		panel.add(JLabel(), c)
		
		buttonList[0].doClick()
		
	}
	
	override fun deactivate(colony: Colony) {
		println("District Ui deactivated")
		ConstructionUiElements.slot.isVisible = false
	}

	override fun addOptions(slot: Any, colony: Colony) {
		if (slot is District) {
			
			val options = ConstructionUiElements.options
			val model = options.model as DefaultTableModel
			
			when(buttonList.getSelected()?.name) {
				"build" -> {
					model.addRow(arrayOf(slot.type, slot.type.time))
				}
				"destroy" -> {
					model.addRow(arrayOf(slot.type, slot.type.time))
				}
				"replace" -> {
					colony.districts.filter { it.type.isTooled }.forEach {
						model.addRow(arrayOf(it.type, it.type.time))
					}
				}
				"tool" -> {
					colony.districtTypes.forEach {
						model.addRow(arrayOf(it, it.time))
					}
				}
				"detool" -> {
					model.addRow(arrayOf(DistrictType.emptyDistrict, DistrictType.emptyDistrict.time))
				}
				"upgrade" -> {
				}
			}
			
		}
	}

	override fun showResources(option: Any) {
		
		if (option is DistrictType) {
			
			val resources = ConstructionUiElements.resources
			val model = resources.model as DefaultTableModel

			model.addRow(arrayOf("\$Cost", "\$"))
			option.cost.forEach {
				model.addRow(arrayOf(it.type.id, it.amount))
			}
			model.addRow(arrayOf("\$Production", "\$"))
			option.production.forEach {
				model.addRow(arrayOf(it.type.id, it.amount))
			}
			model.addRow(arrayOf("\$Upkeep", "\$"))
			option.upkeep.forEach {
				model.addRow(arrayOf(it.type.id, it.amount))
			}

		}
		
	}
	
}
