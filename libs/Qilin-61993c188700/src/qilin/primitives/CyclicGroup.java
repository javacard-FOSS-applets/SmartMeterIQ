package qilin.primitives;

/**
 * Represents a cyclic group (one that is generated by a single element).
 * This interface is an extension of Group. 
 * @author talm
 *
 * @param <G> the group element type
 */
public interface CyclicGroup<G> extends Group<G> {
	/**
	 * Return a generator for the group.
	 */
	public G getGenerator();
}
