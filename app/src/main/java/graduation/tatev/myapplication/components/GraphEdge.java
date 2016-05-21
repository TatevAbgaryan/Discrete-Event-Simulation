package graduation.tatev.myapplication.components;

public class GraphEdge {

    private Terminal sourceTerminal;
    private Terminal targetTerminal;
    private int weight;

    public GraphEdge() {

    }

    public GraphEdge(Terminal t1, Terminal t2) {
        sourceTerminal = t1;
        targetTerminal = t2;
    }


    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Terminal getSourceTerminal() {
        return sourceTerminal;
    }

    public void setSourceTerminal(Terminal sourceTerminal) {
        this.sourceTerminal = sourceTerminal;
    }

    public Terminal getTargetTerminal() {
        return targetTerminal;
    }

    public void setTargetTerminal(Terminal targetTerminal) {
        this.targetTerminal = targetTerminal;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof GraphEdge)) return false;

        if (!this.sourceTerminal.equals(((GraphEdge) obj).sourceTerminal) && !this.sourceTerminal.equals(((GraphEdge) obj).targetTerminal))
            return false;
        if (!this.targetTerminal.equals(((GraphEdge) obj).targetTerminal) && !this.targetTerminal.equals(((GraphEdge) obj).sourceTerminal))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + targetTerminal.hashCode();
        result = 31 * result + sourceTerminal.hashCode();
        return result;
        // return Objects.hashCode(this);
    }
}