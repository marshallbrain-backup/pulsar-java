package ui.colony.construction

import com.marshalldbrain.pulsar.colony.Colony
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
			"Project", "Amount\nRemaining", " % of \n Capacity ", "Production\nRate", "Time\nRemaining", "Estimated Completion\nDate"
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

fun construction(colony: Colony): JPanel {
	
	val constructionPanel = JPanel(BorderLayout())
	
	val leftPanel = JPanel(GridBagLayout())
	val centerPanel = JPanel(GridBagLayout())
	
	leftPanelInit(leftPanel, colony)
	centerPanelInit(centerPanel, colony)
	
	projectDetailInit()
	projectCreateInit()
	projectModifyInit()
	
	constructionPanel.add(leftPanel, BorderLayout.LINE_START)
	constructionPanel.add(centerPanel, BorderLayout.CENTER)
	
	return constructionPanel
	
}

fun leftPanelInit(panel: JPanel, colony: Colony) {
fun projectDetailInit() {
	
	panel.border = border {
		compound(padded(5), titled("Construction Options", line(Color.BLACK)))
	}
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
	panel.add(create, c)
	
}

fun projectModifyInit() {

}

fun centerPanelInit(panel: JPanel, colony: Colony) {

	val c = GridBagConstraints()
	initGridBagConstraints(c)
	
	val allocation = ConstructionUiElements.allocation
	allocation.columnModel.columns.iterator().forEach {
		it.headerRenderer = allocation.tableHeader.defaultRenderer
	}
	
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
	val type = ConstructionUiElements.type
	type.addItemListener { constructionTypeItemListener(it, colony) }
	
	c.gridy = 0
	panel.add(ConstructionUiElements.type, c)
	
	val slot = ConstructionUiElements.slot
	slot.renderer = SlotCellRenderer
	slot.addItemListener { slotTypeItemListener(it, colony) }
	slot.isVisible = false
	
	c.gridy = 1
	c.insets = Insets(2, 4, 2, 4)
	panel.add(ConstructionUiElements.slot, c)
	
	val options = ConstructionUiElements.options
	options.columnModel.getColumn(0).minWidth = 120
	options.columnModel.getColumn(1).minWidth = 20
	options.selectionModel.addListSelectionListener { optionItemListener(it) }
	options.columnModel.columns.asIterator().forEach { it.cellRenderer = OptionsCellRenderer }
	
	c.gridy = 2
	c.weighty = 1.0
	panel.add(createScrollTable(
		ConstructionUiElements.options,
		maxVisibleRows = 15,
		border = border {
			titled("Constructable Items", line(Color.BLACK))
		}
	), c)
	
	val resources = ConstructionUiElements.resources
	resources.columnModel.columns.asIterator().forEach { it.cellRenderer = ResourcesCellRenderer }
	
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

fun constructionTypeItemListener(e: ItemEvent, colony: Colony) {
	
	if (e.stateChange == ItemEvent.DESELECTED) {
		ConstructionUiElements.slot.removeAllItems()
		ConstructionUiElements.options.removeAll()
	}
	
	if (e.stateChange == ItemEvent.SELECTED) {
		
		activeType.deactivate(colony)
		
		activeType = when (e.item) {
			"Districts" -> DistrictUi
			
			else -> {
				EmptyUi
			}
		}
		
		activeType.activate(colony)
		
	}

}

fun slotTypeItemListener(e: ItemEvent, colony: Colony) {
	activeType.addOptions(colony)
}

fun optionItemListener(e: ListSelectionEvent) {

	val options = ConstructionUiElements.options
	val resources = ConstructionUiElements.resources

	val model = resources.model as DefaultTableModel
	model.rowCount = 0

	if (options.selectedRow >= 0 && !e.valueIsAdjusting) {

		val target = options.getValueAt(options.selectedRow, 0)

		activeType.showResources(target)

	}
}

interface ConstructionUi {
	
	fun activate(colony: Colony)
	fun deactivate(colony: Colony)
	fun addOptions(colony: Colony)
	fun showResources(option: Any)

}

object EmptyUi : ConstructionUi {
	
	override fun activate(colony: Colony) {
	}
	
	override fun deactivate(colony: Colony) {
	}

	override fun addOptions(colony: Colony) {
	}

	override fun showResources(option: Any) {
	}

}