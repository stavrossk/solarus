/*
 * Copyright (C) 2006-2014 Christopho, Solarus - http://www.solarus-games.org
 *
 * Solarus Quest Editor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Solarus Quest Editor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.solarus.editor.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.util.*;
import org.solarus.editor.*;
import org.solarus.editor.entities.*;

/**
 * A component to set the view settings of the map editor,
 * i.e. what entities are displayed and how.
 */
public class MapViewSettingsPanel extends JPanel implements Observer {

    /**
     * The options visualised by this component.
     */
    private MapViewSettings settings;

    private JCheckBoxMenuItem showLowLayerCheckBox;
    private JCheckBoxMenuItem showIntermediateLayerCheckBox;
    private JCheckBoxMenuItem showHighLayerCheckBox;
    private JCheckBox showTransparencyCheckBox;
    private JCheckBox showGridCheckBox;
    private ZoomChooser zoomChooser;
    private JCheckBoxMenuItem[] showEntityCheckBoxes;

    /**
     * Constructor.
     * @param settings The map view settings to visualise with this component.
     */
    public MapViewSettingsPanel(MapViewSettings settings) {
        super(new BorderLayout());

        this.settings = settings;

        zoomChooser = new ZoomChooser();
        JPanel centerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5); // margins
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
        constraints.weightx = 1;

        // show/hide layer
        showLowLayerCheckBox = new JCheckBoxMenuItem("Show low layer");
        showLowLayerCheckBox.addItemListener(new ItemListenerLayer(Layer.LOW));

        showIntermediateLayerCheckBox = new JCheckBoxMenuItem("Show intermediate layer");
        showIntermediateLayerCheckBox.addItemListener(new ItemListenerLayer(Layer.INTERMEDIATE));

        showHighLayerCheckBox = new JCheckBoxMenuItem("Show high layer");
        showHighLayerCheckBox.setSelected(settings.getShowLayer(Layer.HIGH));
        showHighLayerCheckBox.addItemListener(new ItemListenerLayer(Layer.HIGH));

        final JPopupMenu showLayerPopupMenu = new JPopupMenu();
        showLayerPopupMenu.add(showLowLayerCheckBox);
        showLayerPopupMenu.add(showIntermediateLayerCheckBox);
        showLayerPopupMenu.add(showHighLayerCheckBox);

        final JButton showLayerButton = new JButton("Show/Hide layer");
        showLayerButton.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent me) {

                showLayerPopupMenu.show(showLayerButton, 0, showLayerButton.getHeight());
            }

            @Override
            public void mousePressed(MouseEvent me) {
            }

            @Override
            public void mouseReleased(MouseEvent me) {
            }

            @Override
            public void mouseEntered(MouseEvent me) {
            }

            @Override
            public void mouseExited(MouseEvent me) {
            }
        });

        constraints.gridx = 0;
        constraints.gridy = 0;
        centerPanel.add(showLayerButton, constraints);

        // show/hide entity type
        final JPopupMenu showEntityPopupMenu = new JPopupMenu();

        EntityType[] types = EntityType.values();
        showEntityCheckBoxes = new JCheckBoxMenuItem[types.length];

        for (int i = 0; i < types.length; i++) {

            EntityType type = types[i];
            showEntityCheckBoxes[i] = new JCheckBoxMenuItem("Show " + types[i].getHumanName());
            showEntityCheckBoxes[i].setSelected(settings.getShowEntityType(type));
            showEntityCheckBoxes[i].addItemListener(new ItemListenerEntity(type));
            showEntityPopupMenu.add(showEntityCheckBoxes[i]);
        }

        final JButton showEntityButton = new JButton("Show/Hide entity type");
        showEntityButton.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent me) {

                showEntityPopupMenu.show(showEntityButton, 0, showEntityButton.getHeight());
            }

            @Override
            public void mousePressed(MouseEvent me) {
            }

            @Override
            public void mouseReleased(MouseEvent me) {
            }

            @Override
            public void mouseEntered(MouseEvent me) {
            }

            @Override
            public void mouseExited(MouseEvent me) {
            }
        });

        constraints.gridx++;
        centerPanel.add(showEntityButton, constraints);

        // show transparency
        showTransparencyCheckBox = new JCheckBox("Show transparency");
        showTransparencyCheckBox.addItemListener(new ItemListenerTransparency());
        constraints.gridx = 0;
        constraints.gridy = 1;
        centerPanel.add(showTransparencyCheckBox, constraints);

        // show grid
        showGridCheckBox = new JCheckBox("Show grid");
        showGridCheckBox.addItemListener(new ItemListenerGrid());
        constraints.gridx++;
        centerPanel.add(showGridCheckBox, constraints);

        add(zoomChooser, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);

        update(settings, null);
        settings.addObserver(this);
    }

    /**
     * Called when settings have changed.
     */
    @Override
    public void update(Observable model, Object obj) {

        if (model instanceof MapViewSettings) {
            showLowLayerCheckBox.setSelected(settings.getShowLayer(Layer.LOW));
            showIntermediateLayerCheckBox.setSelected(settings.getShowLayer(Layer.INTERMEDIATE));
            showTransparencyCheckBox.setSelected(settings.getShowTransparency());
            showGridCheckBox.setSelected(settings.getShowGrid());
            zoomChooser.update();

            for (EntityType type: EntityType.values()) {
                showEntityCheckBoxes[type.ordinal()].setSelected(settings.getShowEntityType(type));
            }
        }
    }

    /**
     * Listener invoked when the state of a layer checkbox has changed.
     */
    private class ItemListenerLayer implements ItemListener {

        /**
         * The layer controlled by this checkbox.
         */
        private Layer layer;

        /**
         * Constructor.
         * @param layer the layer controlled by the checkbox.
         */
        public ItemListenerLayer(Layer layer) {
            this.layer = layer;
        }

        /**
         * Method invoked when the user clicks on the checkbox.
         */
        public void itemStateChanged(ItemEvent itemEvent) {

            // get the new checkbox state
            boolean show = (itemEvent.getStateChange() == ItemEvent.SELECTED);

            // update the options
            settings.setShowLayer(layer, show);
        }
    }

    /**
     * Listener invoked when the state of the grid checkbox has changed.
     */
    private class ItemListenerGrid implements ItemListener {
    	/**
    	 * Constructor.
    	 */
    	public ItemListenerGrid() {

    	}
        /**
         * Method invoked when the user clicks on the checkbox.
         */
        public void itemStateChanged(ItemEvent itemEvent) {

            // get the new checkbox state
            boolean show = (itemEvent.getStateChange() == ItemEvent.SELECTED);

            // update the options
            settings.setShowGrid(show);
        }

    }
    /**
     * Listener invoked when the state of the transparency checkbox has changed.
     */
    private class ItemListenerTransparency implements ItemListener {

        /**
         * Constructor.
         */
        public ItemListenerTransparency() {
        }

        /**
         * Method invoked when the user clicks on the checkbox.
         */
        public void itemStateChanged(ItemEvent itemEvent) {

            // get the new checkbox state
            boolean show = (itemEvent.getStateChange() == ItemEvent.SELECTED);

            // update the options
            settings.setShowTransparency(show);
        }
    }

    /**
     * A component to select the zoom applied to the map view.
     */
    private class ZoomChooser extends JPanel implements ChangeListener {

        private final Zoom[] zooms = Zoom.values();

        private JSlider slider;

        public ZoomChooser() {
            super();

            setBorder(BorderFactory.createTitledBorder("Zoom"));

            slider = new JSlider(0, zooms.length - 1);
            add(slider, BorderLayout.CENTER);

            Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
            int i = 0;
            for (Zoom zoom: zooms) {
                labelTable.put(i++, new JLabel(zoom.getLabel()));
            }
            slider.setLabelTable(labelTable);

            slider.setMajorTickSpacing(10);
            slider.setMinorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
            slider.setSnapToTicks(true);

            slider.addChangeListener(this);
        }

        public void update() {

            Zoom zoom = settings.getZoom();
            slider.setValue(zoom.getIndex());
        }

        @Override
        public void stateChanged(ChangeEvent ev) {
            settings.setZoom(zooms[slider.getValue()]);
        }
    }

    /**
     * Listener invoked when the state of a entity checkbox has changed.
     */
    private class ItemListenerEntity implements ItemListener {

        /**
         * The entity type controlled by this checkbox.
         */
        private final EntityType type;

        /**
         * Constructor.
         * @param type the entity type controlled by the checkbox.
         */
        public ItemListenerEntity(EntityType type) {

            this.type = type;
        }

        /**
         * Method invoked when the user clicks on the checkbox.
         */
        @Override
        public void itemStateChanged(ItemEvent itemEvent) {

            // get the new checkbox state
            boolean show = (itemEvent.getStateChange() == ItemEvent.SELECTED);

            // update the options
            settings.setShowEntityType(type, show);
        }
    }
}

