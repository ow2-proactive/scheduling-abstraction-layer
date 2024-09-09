endpoints readme

# Scheduling Abstraction Layer (SAL) Endpoints

SAL provides multiple endpoints that you can use to interact with the ProActive Scheduler &amp; Resource Manager:

<figure class="table op-uc-figure_align-center op-uc-figure">
    <table class="op-uc-table">
        <thead class="op-uc-table--head">
            <tr class="op-uc-table--row">
                <th class="op-uc-p op-uc-table--cell op-uc-table--cell_head">Category</th>
                <th class="op-uc-p op-uc-table--cell op-uc-table--cell_head">Short Description</th>
            </tr>
        </thead>
        <tbody>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/1-connection-endpoints.md">1- Connection endpoints</a>
                </td>
                <td class="op-uc-p op-uc-table--cell">Establishing connection with ProActive server</td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/2-cloud-endpoints.md">2- Cloud endpoints</a>
                </td>
                <td class="op-uc-p op-uc-table--cell"></td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/3-byon-endpoints.md">3-Byon endpoints</a>
                </td>
                <td class="op-uc-p op-uc-table--cell"></td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/4-edge-endpoints.md">4- Edge endpoints</a>
                </td>
                <td class="op-uc-p op-uc-table--cell"></td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/5-job-endpoints.md">5- Job endpoints</a>
                </td>
                <td class="op-uc-p op-uc-table--cell"></td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/6-monitoring-endpoints.md">6- Monitoring endpoints</a>
                </td>
                <td class="op-uc-p op-uc-table--cell"></td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/7-node-endpoints.md">7- Node endpoints</a>
                </td>
                <td class="op-uc-p op-uc-table--cell"></td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/8-scaling-endpoints.md">8- Scaling endpoints</a>
                </td>
                <td class="op-uc-p op-uc-table--cell"></td>
            </tr>
            <tr class="op-uc-table--row">
                <td class="op-uc-p op-uc-table--cell">
                    <a class="op-uc-link" href="https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/endpoints/9-vault-endpoints">9- Vault endpoints</a>
                </td>
                <td class="op-uc-p op-uc-table--cell"></td>
            </tr>
        </tbody>
    </table>
</figure>

Note that all the endpoints use in their path: **{{protocol}}://{{sal_host}}:{{sal_port}}**

* **{{protocol}}** - refers to the http protocol
* **{{sal_host}}** - host by default is set to localhost
* **{{sal_port}}** - port by default is set to 8088

This default set up might be changed during the [Installation of SAL](https://github.com/ow2-proactive/scheduling-abstraction-layer/blob/master/README.md#2-installation).
