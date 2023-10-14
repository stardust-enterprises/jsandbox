package enterprises.stardust.jsandbox.api.restrict;

public interface RestrictionManager {
    //TODO: move this to
    // enterprises.stardust.jsandbox.defaults.restrict

    Restriction REFLECT = new Restriction("reflect");
    Restriction CLASS_REFLECT = new Restriction("class_reflect", REFLECT);

    Restriction FILE = new Restriction("file");
    Restriction FILE_READ = new Restriction("file_read", FILE);
    Restriction FILE_WRITE = new Restriction("file_write", FILE);

    Restriction NETWORK = new Restriction("network");

    Restriction PROCESS = new Restriction("process");
    Restriction PROCESS_FORK = new Restriction("process_fork");

    Restriction SYSTEM = new Restriction("system");
    Restriction SYSTEM_EXIT = new Restriction("system_exit");

    Restriction THREAD = new Restriction("thread");
    Restriction THREAD_CREATE = new Restriction("thread_create");

}
