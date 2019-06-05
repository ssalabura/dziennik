package schoolregister.Wrapper;

import schoolregister.DataType.Group;

public class GroupWrapper {
    private Group group;

    public GroupWrapper() {}
    public GroupWrapper(Group group){
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }
}
