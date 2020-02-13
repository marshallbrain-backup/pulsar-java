package ui.colony

import com.marshalldbrain.pulsar.colony.Colony
import com.marshalldbrain.pulsar.colony.districts.DistrictType
import com.marshalldbrain.pulsar.resources.Resource
import com.marshalldbrain.pulsar.resources.ResourceMaster
import com.marshalldbrain.pulsar.resources.ResourceType
import ui.colony.construction.construction
import ui.colony.construction.updateAllocation
import ui.util.swing.createScrollTable
import ui.util.swing.createTable
import java.awt.BorderLayout
import java.awt.Button
import java.awt.GridLayout
import java.awt.Panel
import java.awt.Point
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.JTable
import javax.swing.ScrollPaneConstants
import javax.swing.table.DefaultTableModel


val teller = ResourceMaster()
val colony = Colony(initDistrictTypes(), teller)
val frame = JFrame()
val bankFrame = JFrame()

//TODO make updating components easier
//TODO group functions into objects

fun main() {
	
//	title.titleJustification = TitledBorder.CENTER
	
	val panel = Panel(BorderLayout())
	frame.add(panel)
	
	val timeControlPanel = Panel()
	panel.add(timeControlPanel, BorderLayout.PAGE_START)
	
	val time1Day = Button("1 Day")
	time1Day.addActionListener { passTime(1) }
	timeControlPanel.add(time1Day)
	val time30Day = Button("30 Day")
	time30Day.addActionListener { passTime(30) }
	timeControlPanel.add(time30Day)
	
	val tabs = JTabbedPane()
	tabs.addTab("Overview", JPanel())
	tabs.addTab("Construction", construction())
	tabs.addTab("Manage Developments", JPanel())
	tabs.addTab("Manage Population", JPanel())
	tabs.selectedIndex = 1
	panel.add(tabs, BorderLayout.CENTER)
	
	frame.pack()
	frame.setLocationRelativeTo(null);
	frame.isVisible = true
	frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
	
	createResourceFrame()

}

var bank = JTable()

private fun createResourceFrame() {
	
	bank = createTable(
		"Name", "Total Amount", "Income",
		data = listOf("test", 5, 5)
	)
	
	val scrollPane = createScrollTable(bank, alwaysMaxSize = true)
	scrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER
	bankFrame.add(
		scrollPane
	)
	
	bankFrame.pack()
	bankFrame.location = Point(50, 200)
	bankFrame.isVisible = true
	bankFrame.defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
	
}

var timePassed = 0
var month = 0

fun passTime(time: Int) {
	
	timePassed += time
	
	colony.constructionManager.tick(time)
	
	if (timePassed / 30 > month) {
		month = timePassed / 30
		teller.collectResources()
	}
	
	updateAllocation()
	updateBank()
	
}

fun updateBank() {
	
	val model = bank.model as DefaultTableModel
	model.rowCount = 0
	
	teller.bank.forEach { (type, resourcePair) ->
		model.addRow(arrayOf(type.id, resourcePair.first.amount, resourcePair.second.amount))
	}
	
	bankFrame.pack()
	
}

private fun initDistrictTypes() : Set<DistrictType> {
	
	val energy = ResourceType("energy")
	val minerals = ResourceType("minerals")
	val food = ResourceType("food")
	
	val district1 = DistrictType(
		id = "d1",
		time = 100,
		starting = true,
		cost = setOf(Resource(minerals, 100)),
		production = setOf(Resource(energy, 4)),
		upkeep = setOf(Resource(energy, 1))
	)
	
	val district2 = DistrictType(
		id = "d2",
		time = 100,
		cost = setOf(Resource(minerals, 100)),
		production = setOf(Resource(minerals, 4)),
		upkeep = setOf(Resource(energy, 1))
	)
	
	val district3 = DistrictType(
		id = "d3",
		time = 100,
		cost = setOf(Resource(minerals, 100)),
		production = setOf(Resource(food, 4)),
		upkeep = setOf(Resource(energy, 1))
	)
	
	val list = listOf(
		district1,
		district2,
		district3
	)
	
	return list.toSet()
	
}