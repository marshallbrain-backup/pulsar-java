package ui.util.swing

import java.awt.Dimension
import javax.swing.BorderFactory
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.ListSelectionModel
import javax.swing.border.Border
import javax.swing.table.DefaultTableModel

fun createScrollTable(
	table: JTable,
	maxVisibleRows: Int = table.rowCount,
	border: Border = BorderFactory.createEmptyBorder()
) : JScrollPane {
	
	val scrollPane = object : ContinuesScrollPane(table) {
		
		override fun getPreferredSize(): Dimension {
			val insets = border.getBorderInsets(this)
			return Dimension(0, (table.rowHeight)*maxVisibleRows) +
					columnHeader.preferredSize +
					Dimension(insets.left + insets.right, insets.top + insets.bottom)
		}
		
		override fun getMinimumSize(): Dimension = preferredSize
		override fun getMaximumSize(): Dimension = preferredSize
		
		init {
			addMouseWheelListener(ContinuesMouseWheelListener())
		}
		
	}
	
	scrollPane.border = border
	
	return scrollPane
	
}

fun createTable(
	vararg columnNames: String,
	data: MutableList<*> = emptyList<String>().toMutableList()
) : JTable {
	
	val formattedData = getFormattedData(data, *columnNames)
	
	val table = object : JTable(DefaultTableModel(formattedData, columnNames)) {
		override fun isCellEditable(row: Int, column: Int): Boolean = false
	}
	
	table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
	table.cellSelectionEnabled = false
	table.rowSelectionAllowed = true
	table.fillsViewportHeight = true
	
	return table
	
}

private fun getFormattedData(
	data: MutableList<*>,
	vararg columnNames: String
) : Array<Array<*>> {
	
	val colNum = columnNames.size
	val rowNum = data.size / colNum
	
	return List(rowNum) { row ->
		List(colNum) { col ->
			data[row * colNum + col]
		}.toTypedArray()
	}.toTypedArray()
	
}

private operator fun Dimension.plus(i : Dimension) : Dimension {
	return Dimension(width + i.width, height + i.height)
}

