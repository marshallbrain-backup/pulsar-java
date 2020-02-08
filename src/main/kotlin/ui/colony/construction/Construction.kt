package ui.colony.construction

import com.marshalldbrain.pulsar.colony.Colony
import ui.colony.colony
import ui.util.swing.border
import ui.util.swing.createScrollTable
import ui.util.swing.createTable
import ui.util.swing.initGridBagConstraints
import java.awt.BorderLayout
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.ItemEvent
import javax.swing.JButton
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JTextField
import javax.swing.event.ListSelectionEvent
import javax.swing.table.DefaultTableModel

object ConstructionUiElements {
	
	val type: JComboBox<String> = JComboBox(arrayOf("Districts", "Buildings"))
	val slot: JComboBox<Any> = JComboBox()
	
	val options = createTable(
		"Name", "Time"
	)
	val resources = createTable(
		"Resource", "Amount"
	)
	
	val allocation = createTable(
		*listOf(
			"Project", "Production\nRemaining", "Amount\nRemaining", " % of \n Capacity ", "Production\nRate", "Estimated Completion\nDate"
		).map {
			"<html><center>" + it.replace("\n", "<br/>") + "</center></html>"
		}.toTypedArray()
	)
	
	val projectAmount = JTextField("1")
	val projectPercent = JTextField("100")
	val projectDetailPanel = JPanel(GridBagLayout())
	
	val projectCreateButton = JButton("Create")
	val projectCreatePanel = JPanel(GridBagLayout())
	
	val projectModifyButton = JButton("Modify")
	val projectModifyPanel = JPanel(GridBagLayout())
	
}

fun construction(): JPanel {
	
	val constructionPanel = JPanel(BorderLayout())
	
	val leftPanel = JPanel(GridBagLayout())
	val centerPanel = JPanel(GridBagLayout())
	
	leftPanelInit(leftPanel)
	centerPanelInit(centerPanel)
	
	projectDetailInit()
	projectCreateInit()
	projectModifyInit()
	
	constructionPanel.add(leftPanel, BorderLayout.LINE_START)
	constructionPanel.add(centerPanel, BorderLayout.CENTER)
	
	return constructionPanel
	
}

fun projectDetailInit() {
	
	val panel = ConstructionUiElements.projectDetailPanel
	
	val c = GridBagConstraints()
	initGridBagConstraints(c)
	
	val amount = JLabel("Amount of Items")
	c.insets = Insets(4, 4, 4, 4)
	c.anchor = GridBagConstraints.LINE_START
	panel.add(amount, c)
	
	val amountField = ConstructionUiElements.projectAmount
	amountField.columns = 5
	c.gridx = 1
	panel.add(amountField, c)
	
	val percent = JLabel("Percentage of Capacity")
	c.gridx = 2
	panel.add(percent, c)
	
	val percentField = ConstructionUiElements.projectPercent
	percentField.columns = 5
	c.gridx = 3
	panel.add(percentField, c)
	
	c.gridx = 4
	c.weightx = 1.0
	panel.add(JLabel(), c)

}

fun projectCreateInit() {
	
	val panel = ConstructionUiElements.projectCreatePanel
	val c = GridBagConstraints()
	initGridBagConstraints(c)
	
	val create = ConstructionUiElements.projectCreateButton
	create.addActionListener { activeType.createTask(1) }
	panel.add(create, c)
	
}

fun projectModifyInit() {

}

fun centerPanelInit(panel: JPanel) {

	val c = GridBagConstraints()
	initGridBagConstraints(c)
	
	val allocation = ConstructionUiElements.allocation
	allocation.columnModel.columns.iterator().forEach {
		it.headerRenderer = allocation.tableHeader.defaultRenderer
		it.cellRenderer = AllocationCellRenderer
	}
	allocation.columnModel.getColumn(0).preferredWidth = 150
	
	c.gridy = 0
	c.weighty = 1.0
	panel.add(createScrollTable(
		allocation,
		border = border {
			titled("Constructable Items", line(Color.BLACK))
		}
	), c)
	
	val detail = ConstructionUiElements.projectDetailPanel
	
	c.gridy = 1
	c.weighty = 0.0
	c.insets = Insets(2, 4, 2, 4)
	panel.add(detail, c)
	
	val create = ConstructionUiElements.projectCreatePanel
	
	c.gridy = 2
	c.insets = Insets(2, 4, 4, 4)
	panel.add(create, c)
	
}

fun leftPanelInit(panel: JPanel) {
	
	panel.border = border {
		compound(padded(5), titled("Construction Options", line(Color.BLACK)))
	}
	
	val c = GridBagConstraints()
	initGridBagConstraints(c)

	val type = ConstructionUiElements.type
	type.addItemListener { constructionTypeItemListener(it) }
	
	c.gridy = 0
	panel.add(type, c)
	
	val slot = ConstructionUiElements.slot
	slot.renderer = SlotCellRenderer
	slot.addItemListener { slotTypeItemListener(it) }
	slot.isVisible = false
	
	c.gridy = 1
	c.insets = Insets(2, 4, 2, 4)
	panel.add(slot, c)
	
	val options = ConstructionUiElements.options
	options.columnModel.getColumn(0).preferredWidth = 150
	options.selectionModel.addListSelectionListener { optionItemListener(it) }
	options.columnModel.columns.iterator().forEach { it.cellRenderer = OptionsCellRenderer }
	
	c.gridy = 2
	c.weighty = 1.0
	panel.add(createScrollTable(
		options,
		maxVisibleRows = 15,
		border = border {
			titled("Constructable Items", line(Color.BLACK))
		}
	), c)
	
	val resources = ConstructionUiElements.resources
	resources.columnModel.columns.iterator().forEach { it.cellRenderer = ResourcesCellRenderer }
	
	c.gridy = 3
	c.weighty = 0.0
	c.insets = Insets(2, 4, 4, 4)
	panel.add(createScrollTable(
		resources,
		border = border {
			titled("Resources", line(Color.BLACK))
		},
		maxVisibleRows = 8
	), c)
	
	type.selectedIndex = -1
	type.selectedIndex = 0

}

var activeType: ConstructionUi = EmptyUi

fun constructionTypeItemListener(e: ItemEvent) {
	
	if (e.stateChange == ItemEvent.DESELECTED) {
		for(i in 1 until ConstructionUiElements.projectCreatePanel.componentCount){
			ConstructionUiElements.projectCreatePanel.remove(1)
		}
	}
	
	if (e.stateChange == ItemEvent.SELECTED) {
		
		activeType.deactivate()
		
		activeType = when (e.item) {
			"Districts" -> DistrictUi
			
			else -> {
				EmptyUi
			}
		}
		
		activeType.activate()
		
	}

}

fun slotTypeItemListener(e: ItemEvent) {
	
	ConstructionUiElements.projectCreateButton.isEnabled = false
	
	val options = ConstructionUiElements.options
	val model = options.model as DefaultTableModel
	model.setNumRows(0)
	
	if (e.stateChange == ItemEvent.SELECTED) {
		activeType.addOptions(e.item)
	}
	
}

fun optionItemListener(e: ListSelectionEvent) {
	
	val options = ConstructionUiElements.options
	val resources = ConstructionUiElements.resources

	val model = resources.model as DefaultTableModel
	model.rowCount = 0

	if (options.selectedRow >= 0) {
		
		ConstructionUiElements.projectCreateButton.isEnabled = true

		val target = options.getValueAt(options.selectedRow, 0)

		activeType.showResources(target)

	}
}

fun updateAllocation() {
	
	val allocation = ConstructionUiElements.allocation
	
	val model = allocation.model as DefaultTableModel
	model.rowCount = 0
	
	colony.constructionManager.constructionQueue.forEach {
		model.addRow(arrayOf(it.projectName, Function(it::timeRemaining), Function(it::amountRemaining), 100, 1, "None"))
	}
	
}

internal class Function (function: () -> Any?) : () -> Any? by function

interface ConstructionUi {
	
	fun activate()
	fun deactivate()
	fun addOptions(slot: Any)
	fun showResources(option: Any)
	fun createTask(amount: Int)
	
}

object EmptyUi : ConstructionUi {
	
	override fun activate() {
		ConstructionUiElements.projectDetailPanel.isVisible = false
		ConstructionUiElements.projectCreatePanel.isVisible = false
	}
	
	override fun deactivate() {
		ConstructionUiElements.projectDetailPanel.isVisible = true
		ConstructionUiElements.projectCreatePanel.isVisible = true
	}

	override fun addOptions(slot: Any) {
	}

	override fun showResources(option: Any) {
	}
	
	override fun createTask(amount: Int) {
	}
	
}