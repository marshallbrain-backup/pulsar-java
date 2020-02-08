package ui.colony

import com.marshalldbrain.pulsar.colony.Colony
import com.marshalldbrain.pulsar.colony.districts.DistrictType
import com.marshalldbrain.pulsar.resources.Resource
import com.marshalldbrain.pulsar.resources.ResourceType
import ui.colony.construction.construction
import ui.colony.construction.updateAllocation
import java.awt.BorderLayout
import java.awt.Button
import java.awt.Panel
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.JTabbedPane

fun main() {
	
//	title.titleJustification = TitledBorder.CENTER
	
	val colony = Colony(initDistrictTypes())
	
	val frame = JFrame()
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
	tabs.addTab("Construction", construction(colony))
	tabs.addTab("Manage Developments", JPanel())
	tabs.addTab("Manage Population", JPanel())
	tabs.selectedIndex = 1
	panel.add(tabs, BorderLayout.CENTER)
	
	frame.pack()
	frame.setLocationRelativeTo(null);
	frame.isVisible = true
	frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

}

fun passTime(time: Int) {
	
	colony.constructionManager.tick(time)
	updateAllocation()
	
}

private fun initDistrictTypes() : Set<DistrictType> {
	
	val energy = ResourceType("energy")
	val minerals = ResourceType("minerals")
	val food = ResourceType("food")
	
	val district1 = DistrictType(
		id = "d1",
		time = 100,
		starting = true,
		cost = listOf(Resource(minerals, 100)),
		production = listOf(Resource(energy, 4)),
		upkeep = listOf(Resource(energy, 1))
	)
	
	val district2 = DistrictType(
		id = "d2",
		time = 100,
		cost = listOf(Resource(minerals, 100)),
		production = listOf(Resource(minerals, 4)),
		upkeep = listOf(Resource(energy, 1))
	)
	
	val district3 = DistrictType(
		id = "d3",
		time = 100,
		cost = listOf(Resource(minerals, 100)),
		production = listOf(Resource(food, 4)),
		upkeep = listOf(Resource(energy, 1))
	)
	
	val list = listOf(
		district1,
		district2,
		district3
	)
	
	return list.toSet()
	
}