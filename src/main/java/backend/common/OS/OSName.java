package backend.common.OS;

public enum OSName {
    WINDOWS, MAC, LINUX
    // NOTE: There are more operating system (such as Solaris OS), but there is not point
    // on listing them. Note that this is an inflexion point in our implementation, since
    // a mobile port of this program would require to completely modify the way we retrieve
    // the list of installed apps.
}
