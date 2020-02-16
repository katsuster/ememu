package net.katsuster.ememu.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * エミュレータのプロパティパネル。
 *
 * プロパティはラベル、値のほか、設定用のユーザインタフェースも保持します。
 */
public class EmuPropertyPanel extends EmuProperty {
    public static final String URI_FILTER_TITLE = "filter_title";
    public static final String URI_FILTER = "filter";

    private String label;
    private String description;

    private JPanel panel;
    private JCheckBox chkbox;
    private JTextField field;

    public EmuPropertyPanel() {
        this("", TYPE_STRING, "");
    }

    public EmuPropertyPanel(String label, String type, String value) {
        super(type, value);

        this.label = label;

        resetComponents(type);
        setValue(value);
    }

    @Override
    protected void updateForRead() {
        switch (getType()) {
        case TYPE_BOOLEAN:
            setRawValue(Boolean.toString(chkbox.isSelected()));
            break;
        case TYPE_INT:
        case TYPE_URI:
        case TYPE_STRING:
            setRawValue(field.getText());
            break;
        default:
            setRawValue("");
            break;
        }
    }

    @Override
    protected void updateForWrite() {
        switch (getType()) {
        case TYPE_BOOLEAN:
            boolean b = Boolean.parseBoolean(getRawValue());
            chkbox.setSelected(b);
            break;
        case TYPE_INT:
            try {
                int i = Integer.parseInt(getRawValue());
                field.setText(Integer.toString(i));
            } catch (NumberFormatException ex) {
                field.setText("0");
            }
            break;
        case TYPE_URI:
            try {
                URI url = new URI(getRawValue());
                field.setText(url.toString());
            } catch (URISyntaxException ex) {
                field.setText("");
            }
            break;
        case TYPE_STRING:
            field.setText(getRawValue());
            break;
        default:
            break;
        }
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
        resetComponents(getType());
    }

    /**
     * プロパティの説明を取得します。
     *
     * @return プロパティの説明
     */
    public String getDescription() {
        return description;
    }

    /**
     * プロパティの説明を設定します。
     *
     * @param val プロパティの説明
     */
    public void setDescription(String val) {
        description = val;
        resetComponents(getType());
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
            throw new IllegalStateException("Unknown type '" + getType() + "'");
        }

        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    class ButtonListener implements ActionListener {
        private EmuPropertyPanel parent;
        private File lastDir;

        public ButtonListener(EmuPropertyPanel p) {
            parent = p;
            lastDir = new File("");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            File selected = null;
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(lastDir);

            if (!getAttribute(URI_FILTER)[0].isEmpty()) {
                String[] name = getAttribute(URI_FILTER_TITLE);
                String[] filt = getAttribute(URI_FILTER);

                FileNameExtensionFilter filter = new FileNameExtensionFilter(name[0], filt);
                chooser.setFileFilter(filter);
            }
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
