package com.cora.utils;

/**
 * Serializes authenticated Profile tests that mutate the shared test account.
 * Contact Us and FAQ tests run fully in parallel; Profile mutations are exclusive
 * so parallel Chrome browsers do not overwrite each other's profile changes.
 */
public final class ProfileAccountLock {

    private static final Object LOCK = new Object();

    private ProfileAccountLock() {
    }

    public static void runExclusive(Runnable action) {
        synchronized (LOCK) {
            action.run();
        }
    }
}
