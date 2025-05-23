import React from "react"
import { Nav, NavItem, NavLink } from "reactstrap";
import classnames from "classnames";
import { ProtoInstance } from "../../models/instance"
import { Entity } from "../../models/Entity"

interface NavExplorerModelsProps {
    activeTab?: number;
    toggle(tabId: number): void;
    instance?: ProtoInstance;
    models?: Entity[]
}
export default function NavExplorerModels({
    activeTab, toggle, instance, models
}: NavExplorerModelsProps) {
    return ( 
        <>
            {instance &&
                <Nav tabs vertical>
                  <NavItem>
                    <NavLink
                      className={classnames({ active: activeTab === 0 })}
                      onClick={() => toggle(0)}
                    >
                      {instance.name}
                    </NavLink>
                  </NavItem>
                </Nav>
              }
              {models && models.length != 0 && (
              <Nav tabs vertical>
                {models.map((e) => (
                  <NavItem key={e.id}>
                    <NavLink
                      className={classnames({ active: activeTab === e.id })}
                      onClick={() => toggle(e.id)}
                    >
                      {e.name}
                    </NavLink>
                  </NavItem>)
                  )}
              </Nav>)
              }
        </>
    )
}