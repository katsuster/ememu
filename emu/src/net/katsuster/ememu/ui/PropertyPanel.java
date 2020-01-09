package net.katsuster.ememu.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;

public class PropertyPanel {
    public static final String TYPE_BOOLEAN = "Boolean";
    public static final String TYPE_INT = "Int";
    public static final String TYPE_URI = "URI";
    public static final String TYPE_STRING = "String";

    public static final String URI_FILTER_TITLE = "filter_title";
    public static final String URI_FILTER = "filter";

    private String label;
    private String type;

    private JPanel panel;
    private JCheckBox chkbox;
    private JTextField field;

    public PropertyPanel() {
        this("", TYPE_STRING, "");
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
     */
    public void setLabel(String val) {
        label = val;
        resetComponents(type);
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
     */
    public void setType(String val) {
        type = val;
        resetComponents(type);
    }

    /**
     * プロパティの値を取得します。
     *
     * @return プロパティの値
     */
    public String getValue() {
        switch (getType()) {
        case TYPE_BOOLEAN:
            return Boolean.toString(chkbox.isSelected());
        case TYPE_INT:
        case TYPE_URI:
        case TYPE_STRING:
            return field.getText();
        default:
            return "";
        }
    }

    /**
     * プロパティの値を設定します。
     *
     * @param val プロパティの値
     */
    public void setValue(String val) {
        switch (getType()) {
        case TYPE_BOOLEAN:
            setAsBoolean(val);
            break;
        case TYPE_INT:
            setAsInteger(val);
            break;
        case TYPE_URI:
            setAsURI(val);
            break;
        case TYPE_STRING:
            field.setText(val);
            break;
        default:
            break;
        }
    }

    /**
     * プロパティの値を boolean として取得します。
     * "true"（大文字と小文字は区別しない）以外の値の場合 false とみなします。
     *
     * @return プロパティの boolean 値
     */
    public boolean getAsBoolean() {
        return Boolean.parseBoolean(getValue());
    }

    /**
     * プロパティの値として、boolean を設定します。
     * "true"（大文字と小文字は区別しない）以外の値の場合 false とみなします。
     *
     * @param val プロパティの boolean 値として解釈する文字列
     */
    protected void setAsBoolean(String val) {
        chkbox.setSelected(Boolean.parseBoolean(getValue()));
    }

    /**
     * プロパティの値を int として取得します。
     * int への変換に失敗した場合は 0 を返します。
     *
     * @return プロパティの int 値
     */
    public int getAsInteger() {
        try {
            return Integer.parseInt(getValue());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * プロパティの値として、int を設定します。
     * int への変換に失敗した場合は 0 とみなします。
     *
     * @param val プロパティの int 値として解釈する文字列
     */
    protected void setAsInteger(String val) {
        try {
            int i = Integer.parseInt(val);
            field.setText(Integer.toString(i));
        } catch (NumberFormatException ex) {
            field.setText("0");
        }
    }

    /**
     * プロパティの値を URI として取得します。
     * URI への変換に失敗した場合は空の URI を返し、
     * 空の URI の生成にも失敗した場合は null を返します。
     *
     * @return プロパティの URI、もしくは null
     */
    public URI getAsURI() {
        try {
            URI emptyURI = new URI("");

            try {
                return new URI(getValue());
            } catch (URISyntaxException ex) {
                return emptyURI;
            }
        } catch (URISyntaxException e) {
            return null;
        }
    }

    /**
     * プロパティの値として、URI を設定します。
     * URI として解釈できない文字列を渡したときは空文字列とみなします。
     *
     * @param uri プロパティの URI の文字列表現
     */
    protected void setAsURI(String uri) {
        try {
            URI tmp = new URI(uri);
            field.setText(tmp.toString());
        } catch (URISyntaxException ex) {
            field.setText("");
        }
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
        case TYPE_BOOLEAN:
            chkbox = new JCheckBox(getLabel());
            GridBagLayoutHelper.add(panel, layout, chkbox,
                    1, 1, 1, 4);
            break;
        case TYPE_INT:
        case TYPE_STRING:
            field = new JTextField();
            field.setPreferredSize(calcPreferredSize(30));
            field.setMinimumSize(calcPreferredSize(30));
            GridBagLayoutHelper.add(panel, layout, new JLabel(getLabel() + ":"),
                    1, 1, 1, 1);
            GridBagLayoutHelper.add(panel, layout, field,
                    2, 1, 1, 2);
            break;
        case TYPE_URI:
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
