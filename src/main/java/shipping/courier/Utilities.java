package shipping.courier;

import java.util.Optional;

/**
 * Only utility methods.
 */
public final class Utilities {

    private Utilities() { }

    /**
     * Utility method to safe casting any class, possibly giving back an empty Optional.
     * @param candidate the candidate that should be cast
     * @param targetClass the class which the candidate should be cast on
     * @param <S> the type of given candidate
     * @param <T> the type of target class
     * @return the {@link Optional} of {@code targetClass}
     */
    public static <S, T> Optional<T> safeCast(final S candidate, final Class<T> targetClass) {
        return targetClass.isInstance(candidate)
                ? Optional.of(targetClass.cast(candidate))
                : Optional.empty();
    }
}
