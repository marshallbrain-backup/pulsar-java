package ui.util.swing

import java.awt.Color
import javax.swing.BorderFactory
import javax.swing.border.Border
import javax.swing.border.TitledBorder

//val blackLine: Border = BorderFactory.createLineBorder(Color.BLACK)
//val padding: Border = BorderFactory.createEmptyBorder(5, 5, 5, 5)
//val title: TitledBorder = BorderFactory.createTitledBorder(blackLine, "title")

fun border(op: BorderCreator.() -> Border) : Border {
	return op.invoke(BorderCreator())
}
//
class BorderCreator {
	
	
	fun line(color: Color) : Border {
		return BorderFactory.createLineBorder(color)
	}
	
	fun padded(
		padding: Int,
		top: Int = padding,
		left: Int = padding,
		bottom: Int = padding,
		right: Int = padding
	) : Border {
		return BorderFactory.createEmptyBorder(top, left, bottom, right)
	}
	
	fun titled(title: String, border: Border = empty()) : TitledBorder {
		return BorderFactory.createTitledBorder(border, title)
	}
	
	fun compound(outside: Border, inside: Border) : Border {
		return BorderFactory.createCompoundBorder(outside, inside)
	}
	
	fun empty(): Border {
		return BorderFactory.createEmptyBorder()
	}
	
}

//class Compound {
//
//}
//
//fun Border.compound(op: Compound.() -> Unit) = this