/*
 * Copyright 2007 Fred Sauer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.allen_sauer.gwt.dnd.demo.client.example.flextable;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractPositioningDropController;
import com.allen_sauer.gwt.dnd.client.util.CoordinateLocation;
import com.allen_sauer.gwt.dnd.client.util.DOMUtil;
import com.allen_sauer.gwt.dnd.client.util.Location;
import com.allen_sauer.gwt.dnd.client.util.LocationWidgetComparator;
import com.allen_sauer.gwt.dnd.client.util.WidgetLocation;

/**
 * Allows one or more table rows to be dropped into an existing table.
 */
public final class FlexTableRowDropController extends AbstractPositioningDropController {
  private static final String CSS_DEMO_TABLE_POSITIONER = "demo-table-positioner";

  private FlexTable flexTable;
  private IndexedPanel flexTableRowsAsIndexPanel = new IndexedPanel() {
    public Widget getWidget(int index) {
      return flexTable.getWidget(index, 0);
    }

    public int getWidgetCount() {
      return flexTable.getRowCount();
    }

    public int getWidgetIndex(Widget child) {
      throw new UnsupportedOperationException();
    }

    public boolean remove(int index) {
      throw new UnsupportedOperationException();
    }
  };

  private Widget positioner = null;
  private int targetRow;

  public FlexTableRowDropController(FlexTable flexTable) {
    super(flexTable);
    this.flexTable = flexTable;
  }

  public void onDrop(DragContext context) {
    FlexTableRowDragController trDragController = (FlexTableRowDragController) context.dragController;
    FlexTableUtil.moveRow(trDragController.getDraggableTable(), flexTable, trDragController.getDragRow(), targetRow + 1);
    super.onDrop(context);
  }

  public void onEnter(DragContext context) {
    super.onEnter(context);
    positioner = newPositioner(context);
  }

  public void onLeave(DragContext context) {
    positioner.removeFromParent();
    positioner = null;
    super.onLeave(context);
  }

  public void onMove(DragContext context) {
    super.onMove(context);
    targetRow = DOMUtil.findIntersect(flexTableRowsAsIndexPanel, new CoordinateLocation(context.mouseX, context.mouseY),
        LocationWidgetComparator.BOTTOM_HALF_COMPARATOR) - 1;

    Widget w = flexTable.getWidget(targetRow == -1 ? 0 : targetRow, 0);
    Location widgetLocation = new WidgetLocation(w, context.boundaryPanel);
    Location tableLocation = new WidgetLocation(flexTable, context.boundaryPanel);
    context.boundaryPanel.add(positioner, tableLocation.getLeft(), widgetLocation.getTop()
        + (targetRow == -1 ? 0 : w.getOffsetHeight()));
  }

  Widget newPositioner(DragContext context) {
    Widget p = new SimplePanel();
    p.addStyleName(CSS_DEMO_TABLE_POSITIONER);
    p.setPixelSize(flexTable.getOffsetWidth(), 1);
    return p;
  }
}
