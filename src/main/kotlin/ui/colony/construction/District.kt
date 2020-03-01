package ui.colony.construction

import com.marshalldbrain.pulsar.colony.Colony
import com.marshalldbrain.pulsar.colony.construction.ConstructionType
import com.marshalldbrain.pulsar.colony.production.DistrictType
import ui.colony.colony
import ui.colony.updateBank
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
	private var buttonSelected = ""
	
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
		
		buttonSelected = (e.source as JRadioButton).name
		when(buttonSelected) {
			"build" -> { // Districts that have a type
				colony.districtInfo.districts.toList()
			}
			"destroy" -> { // Districts that have a type and an amount above 0
				colony.districtInfo.districts.filter { (_, v) -> v != 0 }.toList()
			}
			"replace" -> { // Same as destroy
				colony.districtInfo.districts.filter { (_, v) -> v != 0 }.toList()
			}
			"tool" -> { // All types that do not have district
				colony.districtInfo.districts.toList() +
						listOf(Pair(DistrictType.emptyDistrict, colony.districtInfo.remaining))
			}
			"detool" -> { // Districts that have a type
				colony.districtInfo.districts.toList()
			}
			"upgrade" -> { // Empty
				emptyList()
			}
			else -> emptyList()
		}.forEach { slot.addItem(it) }
		
	}
	
	override fun activate() {
		
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
	
	override fun deactivate() {
		println("District Ui deactivated")
		ConstructionUiElements.slot.isVisible = false
	}

	override fun addOptions(slot: Any) {
		val type = (slot as Pair<*, *>).first as DistrictType
			
		val options = ConstructionUiElements.options
		val model = options.model as DefaultTableModel
		
		when(buttonSelected) {
			"build" -> {
				model.addRow(arrayOf(type, type.time))
			}
			"destroy" -> {
				model.addRow(arrayOf(type, type.time))
			}
			"replace" -> {
				colony.districtInfo.districts.forEach { (k, _) ->
					model.addRow(arrayOf(k, k.time))
				}
			}
			"tool" -> {
				colony.districtInfo.remainingTypes.forEach { k ->
					model.addRow(arrayOf(k, k.time))
				}
			}
			"detool" -> {
				model.addRow(arrayOf(DistrictType.emptyDistrict, DistrictType.emptyDistrict.time))
			}
			"upgrade" -> {
			}
		}
		
	}

	override fun showResources(option: Any) {
		
		if (option is DistrictType) {
			
			val resources = ConstructionUiElements.resources
			val model = resources.model as DefaultTableModel

			model.addRow(arrayOf("\$Cost", "\$"))
			option.cost.forEach { (k, v) ->
				model.addRow(arrayOf(k.id, v))
			}
			model.addRow(arrayOf("\$Production", "\$"))
			option.production.forEach { (k, v) ->
				model.addRow(arrayOf(k.id, v))
			}
			model.addRow(arrayOf("\$Upkeep", "\$"))
			option.upkeep.forEach { (k, v) ->
				model.addRow(arrayOf(k.id, v))
			}

		}
		
	}
	
	override fun createTask(amount: Int) {
		
		val index = ConstructionUiElements.options.selectedRow
		val type = ConstructionUiElements.options.getValueAt(index, 0)
		val district = ConstructionUiElements.slot.selectedItem
		
		if (district is Pair<*, *> && type is DistrictType) {
			val task = colony.districtInfo.createConstructionTask(type, ConstructionType.BUILD, 1)
			colony.addToConstructionQueue(task)
		}
		
		updateAllocation()
		updateBank()
		
	}
	
}
