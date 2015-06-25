package org.dhbw.geo.backend;

import android.app.DownloadManager;

import org.dhbw.geo.database.DBConditionFence;
import org.dhbw.geo.database.DBFence;
import org.json.JSONObject;

/**
 * Created by Oliver on 18.06.2015.
 * This class exposes methods to access the backend. A fence is defined as a DBFence object and a fence group as a DBConditionFence object.
 */
public class BackendController {

    /**
     * Define a method to call after the REST call has been made.
     */
    private final BackendCallback callback;

    /**
     * Use as such:
     * <pre>
     * <code>BackendController controller = new BackendController(new BackendCallback() {
     *     public void actionPerformed(String result) {
     *         //do stuff on callback with result
     *         ArrayList<DBConditionFence> groups = JSONConverter.getFenceGroups(result);
     *     }
     * }
     * controller.getAllFenceGroups();
     * </code>
     * </pre>
     * @param callback Define a method to call after the REST call has been made.
     */
    public BackendController(BackendCallback callback) {
        this.callback = callback;
    }

    /**
     * Returns all fence groups (DBConditionFence).
     */
    public void getAllFenceGroups() {
        Route route = new Route("/fence_group/getAll", RequestMethod.GET);
        doCall(route);
    }

    /**
     * Creates a fence group.
     * @param fenceGroup
     */
    public void createFenceGroup(DBConditionFence fenceGroup) {
        Route route = new Route("/fence_group", RequestMethod.POST, fenceGroup);
        doCall(route);
    }
    /**
     * Returns a single fence group.
     * @param fence_group_id The serverId of the fence group in question
     */
    public void getFenceGroup(int fence_group_id) {
        Route route = new Route("/fence_group/" + fence_group_id, RequestMethod.GET);
        doCall(route);
    }

    /**
     * Returns all fences for a single group.
     * @param fence_group_id The serverId of the fence group in question
     */
    public void getFencesForGroup(int fence_group_id) {
        Route route = new Route("/fence_group/" + fence_group_id + "/getFences", RequestMethod.GET);
        doCall(route);
    }

    /**
     * Updates a single fence group.
     * @param fence_group_id The serverId of the fence group in question
     * @param fenceGroup The DBConditionFence to update.
     */
    public void updateFenceGroup(int fence_group_id, DBConditionFence fenceGroup) {
        Route route = new Route("/fence_group/" + fence_group_id, RequestMethod.PUT, fenceGroup);
        doCall(route);
    }

    /**
     * Deletes a single fence group.
     * @param fence_group_id The serverId of the fence group in question
     */
    public void deleteFenceGroup(int fence_group_id) {
        Route route = new Route("/fence_group/" + fence_group_id, RequestMethod.DELETE);
        doCall(route);
    }

    /**
     * Creates a single fence.
     * @param fence_group_id The serverId of the fence group in question
     * @param fence The client side fence_id
     */
    public void createFence(int fence_group_id, DBFence fence) {
        Route route = new Route("/fence_group/" + fence_group_id + "/fence", RequestMethod.POST, fence);
        doCall(route);
    }

    /**
     * Returns a single fence.
     * @param fence_group_id The serverId of the fence group in question
     * @param fence_id The client side fence_id
     */
    public void getFence(int fence_group_id, int fence_id) {
        Route route = new Route("/fence_group/" + fence_group_id + "/fence/" + fence_id, RequestMethod.GET);
        doCall(route);
    }

    /**
     * Updates a single fence.
     * @param fence_group_id The serverId of the fence group in question
     * @param fence_id The client side fence_id
     * @param fence The DBFence to update
     */
    public void updateFence(int fence_group_id, int fence_id, DBFence fence) {
        Route route = new Route("/fence_group/" + fence_group_id + "/fence/" + fence_id, RequestMethod.PUT, fence);
        doCall(route);
    }

    /**
     * Deletes a single fence.
     * @param fence_group_id The serverId of the fence group in question
     * @param fence_id The DBFence to delete
     */
    public void deleteFence(int fence_group_id, int fence_id) {
        Route route = new Route("/fence_group/" + fence_group_id + "/fence/" + fence_id, RequestMethod.DELETE);
        doCall(route);
    }

    /**
     * Does the actual REST API call by constructing an APICaller object.
     * @param route The specific API call to make
     */
    private void doCall(Route route) {
        APICaller caller = new APICaller(route, callback);
        caller.execute();
    }

}
