import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class NeutronMonteCarloHistogram extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final Random random = new Random();
    private static final int INITIAL_NEUTRONS = 10000;

    private static final double PROB_ABSORBED = 0.3;
    private static final double PROB_FISSION = 0.5;
    private static final double PROB_ESCAPE = 0.2;  // Now used
    private static final int MEAN_NEUTRONS_PER_FISSION = 2;

    private final List<Integer> neutronsPerGeneration;

    public NeutronMonteCarloHistogram(List<Integer> data) {
        this.neutronsPerGeneration = data;
        setPreferredSize(new Dimension(800, 600));
    }

    public static void main(String[] args) {
        List<Integer> generationData = runSimulation();
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Neutron Chain Reaction Histogram");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new NeutronMonteCarloHistogram(generationData));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
        System.out.println("Finished");
    }

    private static List<Integer> runSimulation() {
        Queue<Integer> queue = new ArrayDeque<>();
        List<Integer> generations = new ArrayList<>();

        queue.add(INITIAL_NEUTRONS);

        while (!queue.isEmpty()) {
            int neutrons = queue.poll();
            if (neutrons == 0) break;
            generations.add(neutrons);
            int newNeutrons = 0;

            for (int i = 0; i < neutrons; i++) {
                double r = random.nextDouble();
                if (r < PROB_ABSORBED) {
                    // Neutron absorbed
                } else if (r < PROB_ABSORBED + PROB_FISSION) {
                    // Neutron causes fission
                    newNeutrons += poisson(MEAN_NEUTRONS_PER_FISSION);
                } else if (r < PROB_ABSORBED + PROB_FISSION + PROB_ESCAPE) {
                    // Neutron escapes
                } else {
                    // Error in probability setup
                    throw new RuntimeException("Total probabilities exceed 1.0");
                }
            }

            queue.add(newNeutrons);
        }

        return generations;
    }

    private static int poisson(double mean) {
        double L = Math.exp(-mean);
        int k = 0;
        double p = 1.0;
        do {
            k++;
            p *= random.nextDouble();
        } while (p > L);
        return k - 1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (neutronsPerGeneration.isEmpty()) return;

        Graphics2D g2 = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();
        System.out.println(""+width+","+height);
        int padding = 40;

        int numBars = neutronsPerGeneration.size();
        System.out.println("numBars="+numBars);
        double barWidth = (width - 2 * padding) / (double)numBars;
        int maxNeutrons = Collections.max(neutronsPerGeneration);
        if (maxNeutrons == 0) return;

        for (int i = 0; i < numBars; i++) {
            int value = neutronsPerGeneration.get(i);
            int barHeight = Math.max(1, (int) ((double) value / maxNeutrons * (height - 2 * padding)));
            int x = (int)(padding + i * barWidth);
            int y = height - padding - barHeight;

            g2.setColor(Color.BLUE);
            g2.fillRect(x, y, (int)Math.max(1,barWidth), barHeight);
            System.out.print(" " + (barWidth) +","+barHeight);
        }

        g2.setColor(Color.BLACK);
        g2.drawString("Generation", width / 2 - 30, height - 10);
        g2.drawString("Neutrons", 10, padding - 10);
    }
}
