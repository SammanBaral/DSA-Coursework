package question6;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;





public class huhu extends JFrame {
    private JTextField urlField1 = new JTextField("");
    private JTextField urlField2 = new JTextField("");
    private JTextField urlField3 = new JTextField("");
    private JButton downloadButton = new JButton("Download All");
    private JButton pauseResumeButton = new JButton("Pause/Resume");
    private JButton cancelButton = new JButton("Cancel");
    private DefaultListModel<DownloadInfo> listModel = new DefaultListModel<>();
    private JList<DownloadInfo> downloadList = new JList<>(listModel);
    private ExecutorService downloadExecutor = Executors.newFixedThreadPool(10); // 10 concurrent downloads

    private Color darkGrayColor = new Color(64, 64, 64);
    private Color lightGrayColor = new Color(192, 192, 192);
    private Font robotoPlain18 = new Font("Roboto", Font.PLAIN, 18);

    public huhu() {
        super("Image Downloader");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 600);
        layoutComponents();
        setVisible(true);
        setLocationRelativeTo(null);
    }

    private void layoutComponents() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        Font robotoBold24 = new Font("Roboto", Font.BOLD, 24);
        Color whiteColor = Color.WHITE;

        getContentPane().setBackground(darkGrayColor);

        JLabel titleLabel = new JLabel("Image Downloader");
        titleLabel.setFont(robotoBold24);
        titleLabel.setBackground(Color.white);
        titleLabel.setForeground(Color.white);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel downloadPanel = new JPanel(new GridLayout(3, 1));

        JPanel addPanel1 = createDownloadPanel(urlField1);
        JPanel addPanel2 = createDownloadPanel(urlField2);
        JPanel addPanel3 = createDownloadPanel(urlField3);

        downloadPanel.add(addPanel1);
        downloadPanel.add(addPanel2);
        downloadPanel.add(addPanel3);

        JScrollPane scrollPane = new JScrollPane(downloadList);
        downloadList.setCellRenderer(new DownloadListRenderer());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(darkGrayColor);

        downloadButton.setFont(robotoPlain18);
        downloadButton.setBackground(lightGrayColor);
        downloadButton.setForeground(darkGrayColor);

        pauseResumeButton.setFont(robotoPlain18);
        pauseResumeButton.setBackground(lightGrayColor);
        pauseResumeButton.setForeground(darkGrayColor);

        cancelButton.setFont(robotoPlain18);
        cancelButton.setBackground(lightGrayColor);
        cancelButton.setForeground(darkGrayColor);

        buttonPanel.add(downloadButton);
        buttonPanel.add(pauseResumeButton);
        buttonPanel.add(cancelButton);

        downloadButton.addActionListener(e -> {
            addDownload(urlField1.getText().trim(), "downloads1");
            addDownload(urlField2.getText().trim(), "downloads2");
            addDownload(urlField3.getText().trim(), "downloads3");
        });

        pauseResumeButton.addActionListener(e -> {
            for (int i = 0; i < listModel.size(); i++) {
                DownloadInfo info = listModel.getElementAt(i);
                if (info != null) {
                    info.togglePauseResume();
                }
            }
        });

        cancelButton.addActionListener(e -> {
            for (int i = 0; i < listModel.size(); i++) {
                DownloadInfo info = listModel.getElementAt(i);
                if (info != null) {
                    info.cancel();
                    listModel.removeElement(info);
                }
            }
        });

        setLayout(new BorderLayout());
        add(downloadPanel, BorderLayout.NORTH);
        add(titleLabel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createDownloadPanel(JTextField urlField) {
        JPanel addPanel = new JPanel();
        addPanel.setBackground(darkGrayColor);
        addPanel.setLayout(new BorderLayout());

        urlField.setFont(robotoPlain18);
        urlField.setForeground(Color.black);

        addPanel.add(urlField, BorderLayout.CENTER);

        return addPanel;
    }

    private void addDownload(String url, String folderName) {
        try {
            new URL(url);
            DownloadInfo info = new DownloadInfo(url);
            listModel.addElement(info);
            DownloadTask task = new DownloadTask(info, folderName, () -> SwingUtilities.invokeLater(this::repaint));
            info.setFuture(downloadExecutor.submit(task));
        } catch (MalformedURLException ex) {
            JOptionPane.showMessageDialog(this, "Invalid URL: " + url, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(huhu::new);
    }
}


class DownloadInfo {
    private final String url;
    private volatile String status = "Waiting...";
    private volatile long totalBytes = 0L;
    private volatile long downloadedBytes = 0L;
    private Future<?> future;
    private final AtomicBoolean paused = new AtomicBoolean(false);

    public DownloadInfo(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public synchronized boolean isPaused() {
        return paused.get();
    }

    public synchronized void togglePauseResume() {
        paused.set(!paused.get());
        notifyAll();
    }

    public String getStatus() {
        return status;
    }

    public synchronized void setStatus(String status) {
        this.status = status;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public void cancel() {
        if (future != null)
            future.cancel(true);
    }

    public synchronized void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public synchronized void addDownloadedBytes(long bytes) {
        this.downloadedBytes += bytes;
    }

    public long getDownloadedBytes() {
        return downloadedBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }
}

class DownloadTask implements Callable<Void> {
    private final DownloadInfo info;
    private final String folderName;
    private final Runnable updateUI;

    public DownloadTask(DownloadInfo info, String folderName, Runnable updateUI) {
        this.info = info;
        this.folderName = folderName;
        this.updateUI = updateUI;
    }

    @Override
    public Void call() throws Exception {
        info.setStatus("Downloading");

        URL url = new URL(info.getUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        long fileSize = connection.getContentLengthLong();
        info.setTotalBytes(fileSize);

        try (InputStream in = new BufferedInputStream(connection.getInputStream())) {
            Path targetPath = Paths.get(folderName, new File(url.getPath()).getName());
            Files.createDirectories(targetPath.getParent());
            try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(targetPath))) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    synchronized (info) {
                        while (info.isPaused())
                            info.wait();
                    }
                    out.write(buffer, 0, bytesRead);
                    info.addDownloadedBytes(bytesRead);
                    updateUI.run();
                    Thread.sleep(200);
                }
                info.setStatus("Completed");
            }
        } catch (IOException | InterruptedException e) {
            info.setStatus("Error: " + e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        } finally {
            updateUI.run();
        }
        return null;
    }
}

class DownloadListRenderer extends JPanel implements ListCellRenderer<DownloadInfo> {
    @Override
    public Component getListCellRendererComponent(JList<? extends DownloadInfo> list, DownloadInfo value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        this.removeAll();
        setLayout(new BorderLayout());
        JLabel urlLabel = new JLabel(value.getUrl());
        JProgressBar progressBar = new JProgressBar(0, 100);
        if (value.getTotalBytes() > 0) {
            int progress = (int) ((value.getDownloadedBytes() * 100) / value.getTotalBytes());
            progressBar.setValue(progress);
        }
        JLabel statusLabel = new JLabel(value.getStatus());
        add(urlLabel, BorderLayout.NORTH);
        add(progressBar, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        return this;
    }
}