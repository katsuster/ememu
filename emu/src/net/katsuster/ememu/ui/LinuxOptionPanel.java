package net.katsuster.ememu.ui;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;

/**
 * Linux 起動時に渡すオプションを設定するパネル。
 *
 * @author katsuhiro
 */
public class LinuxOptionPanel extends JPanel {
    private LinuxOption opts;

    private JTextField txtImage, txtInitrd, txtCmdline;
    private File lastDir;

    /**
     * Linux 起動時に渡すオプションを設定するパネルを作成します。
     */
    public LinuxOptionPanel() {
        this(new LinuxOption());
    }

    /**
     * Linux 起動時に渡すオプションを設定するパネルを作成します。
     *
     * パネル内の各コンポーネントには指定したデフォルト値が設定されます。
     *
     * @param options オプションのデフォルト値
     */
    public LinuxOptionPanel(LinuxOption options) {
        super(true);

        opts = options;

        //最後に開いていたディレクトリを作成する
        lastDir = new File("");

        //コンポーネントを作成
        ButtonListener listenButton = new ButtonListener(this);

        //Get prefered size(40 chars)
        JTextField tmpTxt = new JTextField(String.format("%040x", 0));
        Dimension dim = tmpTxt.getPreferredSize();

        txtImage = new JTextField(opts.getKernelImage().toString());
        txtImage.setPreferredSize(dim);
        txtImage.setMinimumSize(dim);
        JButton chooseKernel = new JButton("...");
        chooseKernel.addActionListener(listenButton);
        chooseKernel.setActionCommand("chooseKernel");

        txtInitrd = new JTextField(opts.getInitrdImage().toString());
        txtInitrd.setPreferredSize(dim);
        txtInitrd.setMinimumSize(dim);
        JButton chooseInitrd = new JButton("...");
        chooseInitrd.addActionListener(listenButton);

        chooseInitrd.setActionCommand("chooseInitrd");
        txtCmdline = new JTextField(opts.getCommandLine());
        txtCmdline.setPreferredSize(dim);
        txtCmdline.setMinimumSize(dim);

        //コンポーネントを配置
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);

        GridBagLayoutHelper.add(this, layout, new JLabel("Kernel Image:", SwingConstants.RIGHT),
                0, 0, 1, 1);
        GridBagLayoutHelper.add(this, layout, txtImage,
                1, 0, 1, 1);
        GridBagLayoutHelper.add(this, layout, chooseKernel,
                2, 0, GridBagConstraints.RELATIVE, 1);
        GridBagLayoutHelper.add(this, layout, new JLabel("Initrd Image:", SwingConstants.RIGHT),
                0, 1, 1, 1);
        GridBagLayoutHelper.add(this, layout, txtInitrd,
                1, 1, 1, 1);
        GridBagLayoutHelper.add(this, layout, chooseInitrd,
                2, 1, GridBagConstraints.RELATIVE, 1);
        GridBagLayoutHelper.add(this, layout, new JLabel("Command line:", SwingConstants.RIGHT),
                0, 2, 1, 1);
        GridBagLayoutHelper.add(this, layout, txtCmdline,
                1, 2, 2, 1);

        setBorder(BorderFactory.createTitledBorder("Linux Boot Options"));
    }

    /**
     * Linux 起動時に渡すオプションを取得します。
     *
     * @return エミュレータで Linux 起動時に渡すオプション
     */
    public LinuxOption getOption() {
        updateOption();

        return opts;
    }

    /**
     * パネル内の各コンポーネントの設定値をオプションオブジェクトに反映させます。
     *
     * 設定値が URI として（ホスト名の場合）解釈できない場合は、
     * オプションオブジェクトの値は変更されません。
     */
    protected void updateOption() {
        try {
            opts.setKernelImage(new URI(txtImage.getText()));
            opts.setInitrdImage(new URI(txtInitrd.getText()));
            opts.setCommandLine(txtCmdline.getText());
        } catch (URISyntaxException e) {
            //ignored
        }
    }

    class ButtonListener implements ActionListener {
        LinuxOptionPanel parent;

        public ButtonListener(LinuxOptionPanel c) {
            parent = c;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            File selected = null;
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(lastDir);

            if (e.getActionCommand().equals("chooseKernel")) {
                int res = chooser.showOpenDialog(parent);
                if (res == JFileChooser.APPROVE_OPTION) {
                    selected = chooser.getSelectedFile();
                    txtImage.setText(selected.toURI().toString());
                }
            }
            if (e.getActionCommand().equals("chooseInitrd")) {
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "All Initrd image", "cpio", "gz");
                chooser.setFileFilter(filter);
                int res = chooser.showOpenDialog(parent);
                if (res == JFileChooser.APPROVE_OPTION) {
                    selected = chooser.getSelectedFile();
                    txtInitrd.setText(selected.toURI().toString());
                }
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
