import React from "react";
import {
  Collapse,
  Navbar,
  NavbarBrand,
  Nav,
  NavItem,
  NavLink,
} from "reactstrap";

export default function HeaderNav() {
  return (
    <Navbar color="faded" light expand="md">
      <NavbarBrand href="/" className="mr-auto">
        КОНФИГУРАТОР
      </NavbarBrand>
      <Collapse isOpen={true} navbar>
        <Nav className="mr-0 ml-auto" navbar>
          <NavItem>
            <NavLink href="/explorer">Обозреватель</NavLink>
          </NavItem>
          <NavItem>
            <NavLink href="/tools/model">Генератор моделей</NavLink>
          </NavItem>
          <NavItem>
            <NavLink href="/configurator">Моделирование конфигураций</NavLink>
          </NavItem>
        </Nav>
      </Collapse>
    </Navbar>
  );
}
