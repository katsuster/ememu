package net.katsuster.ememu.ui;

import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.*;

/**
 * Linux 起動時に渡すオプションを設定するパネル。
 *
 * @author katsuhiro
 */
public class LinuxOptionPanel extends JPanel {
    private LinuxOption opts;

    private JTextField txtImage, txtInitram, txtCmdline;

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
        super(new GridLayout(3, 2, 5, 5), true);

        opts = options;
        txtImage = new JTextField(opts.getKernelImage().toString());
        txtInitram = new JTextField(opts.getInitramfsImage().toString());
        txtCmdline = new JTextField(opts.getCommandLine());

        add(new JLabel("Kernel Image", SwingConstants.RIGHT));
        add(txtImage);
        add(new JLabel("InitramFS Image", SwingConstants.RIGHT));
        add(txtInitram);
        add(new JLabel("Command line", SwingConstants.RIGHT));
        add(txtCmdline);
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
     */
    protected void updateOption() {
        try {
            opts.setKernelImage(new URI(txtImage.getText()));
            opts.setInitramfsImage(new URI(txtInitram.getText()));
            opts.setCommandLine(txtCmdline.getText());
        } catch (URISyntaxException e) {
            //ignored
        }
    }
}
