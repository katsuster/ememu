package net.katsuster.ememu.ui;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class PropertyPanel {
    private String label;
    private String type;

    private JPanel panel;
    private JCheckBox chkbox;
    private JTextField field;

    public PropertyPanel() {
        this("", "String", "");
    }

    public PropertyPanel(String label, String type, String value) {
        this.label = label;
        this.type = type;

        resetComponents(type);
        setValue(value);
    }

    /**
     * プロパティのラベルを取得します。
     *
     * @return プロパティのラベル
     */
    public String getLabel() {
        return label;
    }

    /**
     * プロパティのラベルを設定します。
     *
     * @param val プロパティのラベル
     * @return 以前の値
     */
    public String setLabel(String val) {
        resetComponents(type);

        String before = label;
        label = val;
        return before;
    }

    /**
     * プロパティの値の型を取得します。
     *
     * @return プロパティの値の型
     */
    public String getType() {
        return type;
    }

    /**
     * プロパティの値の方を設定します。
     *
     * @param val プロパティの値の型
     * @return 以前の値
     */
    public String setType(String val) {
        resetComponents(val);

        String before = type;
        type = val;
        return before;
    }

    /**
     * プロパティの値を取得します。
     *
     * @return プロパティの値
     */
    public String getValue() {
        switch (getType()) {
        case "Boolean":
            return Boolean.toString(chkbox.isSelected());
        case "String":
        case "File":
            return field.getText();
        default:
            return "";
        }
    }

    /**
     * プロパティの値を設定します。
     *
     * @param val プロパティの値
     * @return 以前の値
     */
    public String setValue(String val) {
        String before;

        switch (getType()) {
        case "Boolean":
            before = Boolean.toString(chkbox.isSelected());
            chkbox.setSelected(Boolean.parseBoolean(val));
            break;
        case "String":
        case "File":
            before = field.getText();
            field.setText(val);
            break;
        default:
            return "";
        }

        return before;
    }

    /**
     * 設定用のコンポーネントを取得します。
     *
     * @return 設定用のコンポーネント
     */
    public JPanel getComponent() {
        return panel;
    }

    /**
     * "0" を指定された数だけ並べたときのコンポーネントの推奨サイズを取得します。
     *
     * @param chars 文字を並べる個数
     * @return コンポーネントのサイズ
     */
    protected Dimension calcPreferredSize(int chars) {
        JTextField tmpTxt = new JTextField(
                String.format("%0" + Integer.toString(chars) + "x", 0));
        Dimension d = tmpTxt.getPreferredSize();
        //d.height = d.height * 6 / 5;
        return d;
    }

    /**
     * UI コンポーネントを再生成します
     *
     * @param val プロパティの値の型
     */
    protected void resetComponents(String val) {
        panel = new JPanel();

        GridBagLayout layout = new GridBagLayout();
        panel.setLayout(layout);

        switch (val) {
        case "Boolean":
            chkbox = new JCheckBox(getLabel());
            GridBagLayoutHelper.add(panel, layout, chkbox,
                    1, 1, 1, 4);
            break;
        case "String":
            field = new JTextField();
            field.setPreferredSize(calcPreferredSize(30));
            field.setMinimumSize(calcPreferredSize(30));
            GridBagLayoutHelper.add(panel, layout, new JLabel(getLabel() + ":"),
                    1, 1, 1, 1);
            GridBagLayoutHelper.add(panel, layout, field,
                    2, 1, 1, 2);
            break;
        case "File":
            field = new JTextField();
            field.setPreferredSize(calcPreferredSize(30));
            field.setMinimumSize(calcPreferredSize(30));

            JButton btn = new JButton("...");
            ButtonListener l = new ButtonListener(this);
            btn.addActionListener(l);
            btn.setActionCommand("choose");
            btn.setPreferredSize(calcPreferredSize(5));

            GridBagLayoutHelper.add(panel, layout, new JLabel(getLabel() + ":"),
                    1, 1, 1, 1);
            GridBagLayoutHelper.add(panel, layout, field,
                    2, 1, 1, 2);
            GridBagLayoutHelper.add(panel, layout, btn,
                    4, 1, 1, 1);
            break;
        default:
            break;
        }

        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    class ButtonListener implements ActionListener {
        private PropertyPanel parent;
        private File lastDir;

        public ButtonListener(PropertyPanel p) {
            parent = p;
            lastDir = new File("");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            File selected = null;
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(lastDir);

            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Binary files (*.bin)", "bin");
            chooser.setFileFilter(filter);
            int res = chooser.showOpenDialog(parent.getComponent());
            if (res == JFileChooser.APPROVE_OPTION) {
                selected = chooser.getSelectedFile();
                parent.setValue(selected.toURI().toString());
            }

            //最後に開いていたディレクトリを更新
            if (selected != null) {
                if (selected.isDirectory()) {
                    lastDir = selected;
                } else if (selected.isFile() || selected.getParent() != null) {
                    lastDir = selected.getParentFile();
                }
            }
        }
    }
}
