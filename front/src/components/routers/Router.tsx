import React from "react"
import { BrowserRouter as Router, Switch, Route } from "react-router-dom";
import AssembleModels from "../../pages/AssembleModels";
import Configurator from "../../pages/Configurator";
import ExplorerInstances from "../../pages/ExplorerInstances";

export default function MainRouter() {
    return (
      <Router>
        <Switch>
          <Route 
            path={["/explorer/:instanceId", "/explorer"]}
            component={ExplorerInstances}/>
          <Route path="/tools/model">
            <AssembleModels />
          </Route>
          <Route path="/configurator">
              <Configurator/>
          </Route>
        </Switch>
      </Router>
    );
}

/*interface InnerRouterProps {
    instance: Instance
}
export function InnerRouter({instance}: InnerRouterProps) {
    return (
    <Router>
        <Switch>
          
        </Switch>
    </Router>
    );
}*/