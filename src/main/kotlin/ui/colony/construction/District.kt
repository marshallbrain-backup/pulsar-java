package ui.colony.construction

import com.marshalldbrain.pulsar.colony.Colony
import com.marshalldbrain.pulsar.colony.districts.DistrictType
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
		JRadioButton("Tool"),
		JRadioButton("Detool"),
		JRadioButton("Upgrade")
	)
	private val buttonGroup = ButtonGroup()
	
	init {
		buttonList.forEach { b ->
			buttonGroup.add(b)
			b.addActionListener { createRadioButtonListener(it) }
		}
	}
	
	private fun createRadioButtonListener(it: ActionEvent) {
		println((it.source as JRadioButton).text)
	}
	
	override fun activate(colony: Colony) {
		
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
		val untooledSlots = colony.untooledDistricts
		if (untooledSlots.isNotEmpty()) {
			slot.addItem(untooledSlots)
		}
		colony.districts.filter { it.type.isTooled }.forEach { slot.addItem(it) }
		
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

	override fun addOptions(colony: Colony) {

		val options = ConstructionUiElements.options
		val model = options.model as DefaultTableModel

		colony.districtTypes.forEach { model.addRow(arrayOf(it, it.time)) }

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
	
	override fun getCreateMode(): JRadioButton? {
		return buttonList.find { it.isSelected }
	}

}