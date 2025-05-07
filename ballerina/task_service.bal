
public type Service distinct service object {
    remote function onTrigger() returns error?;
};
