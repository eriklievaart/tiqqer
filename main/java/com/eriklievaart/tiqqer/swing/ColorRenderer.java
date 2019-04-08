package com.eriklievaart.tiqqer.swing;

import java.awt.Color;
import java.awt.Component;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ColorRenderer implements TableCellRenderer {

	private TiqqerTableModel model;

	public ColorRenderer(TiqqerTableModel model) {
		this.model = model;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int col) {

		JLabel label = new JLabel(value.toString());
		label.setOpaque(true);
		label.setBackground(Color.BLACK);
		label.setForeground(getColor(row));
		return label;
	}

	private Color getColor(int row) {
		LogRecord record = model.getRow(row);
		if (record.getLevel() == Level.SEVERE) {
			return Color.RED;
		}
		if (record.getLevel() == Level.WARNING) {
			return Color.YELLOW;
		}
		if (record.getLevel() == Level.FINE) {
			return Color.GRAY;
		}
		if (record.getLevel() == Level.FINER || record.getLevel() == Level.FINEST) {
			return Color.DARK_GRAY;
		}
		return Color.WHITE;
	}
}
