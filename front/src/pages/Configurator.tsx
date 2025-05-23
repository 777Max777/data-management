import React from "react"
import { Dropdown, DropdownToggle, DropdownMenu, DropdownItem, Container, Row, Col } from 'reactstrap';

export default function Configurator() {
  const menuItems = ["Задачи", "Сценарии"];
  const mode: string = "Режим";
  const [dropdownOpen, setDropdownOpen] = React.useState(false);
  const [dropDownName, setDropDownName] = React.useState<string | null>();
  const toggle = () => setDropdownOpen(prevState => !prevState);

  const onClickHandler = event => setDropDownName((event.target as Element).textContent)

  return (
    <Container fluid >
        <Row>
            <Col sm={{size:6, order:2, offset:1}}>
                <Dropdown isOpen={dropdownOpen} toggle={toggle}>
                <DropdownToggle caret>
                    {dropDownName ? dropDownName : mode}
                </DropdownToggle>
                <DropdownMenu>
                    <DropdownItem header>{mode}</DropdownItem>
                    {menuItems.map((name, key) => <DropdownItem
                                                    key={key}
                                                    onClick={onClickHandler}
                                                    >{name}
                                                </DropdownItem>)
                    }
                </DropdownMenu>
                </Dropdown>
            </Col>
        </Row>
    </Container>
  );
}