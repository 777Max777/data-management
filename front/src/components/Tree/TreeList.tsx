import React from "react";
import TreeItem from "./TreeItem";
import "./TreeView.css";
import { Entity } from "../../models/Entity";

interface TreeListProps {
  onChoosenItem(a: HTMLAnchorElement): void;
  entity: Entity;
  display: boolean;
}
export const TreeList: React.FC<TreeListProps> = (props) => {
  const [isChildOpened, setChildOpened] = React.useState(false);
  //const [isChecked, setChecked] = React.useState(false);
  const itemRef = React.useRef<HTMLAnchorElement>(null);

  const iconClass = "indicator glyphicon ";
  function onClickChildBranch() {
    setChildOpened(!isChildOpened);
  }

  const changeState = () => props.onChoosenItem(itemRef.current!);

  return (
    <>
      {props.entity.children &&
      props.entity.children != null &&
      props.entity.children.length ? (
        <li className={props.display ? "branch opened" : "branch closed"}>
          <i
            className={
              isChildOpened
                ? iconClass + "glyphicon-minus-sign"
                : iconClass + "glyphicon-plus-sign"
            }
            onClick={onClickChildBranch}
          />
          <a ref={itemRef} href="#" onClick={changeState}>
            {props.entity.name}
          </a>
          <ul>
            {props.entity.children.map((c: Entity, index: number) => {
              return (
                <TreeList
                  key={index}
                  entity={c}
                  display={isChildOpened}
                  onChoosenItem={props.onChoosenItem}
                />
              );
            })}
          </ul>
        </li>
      ) : (
        <TreeItem
          id={props.entity.id}
          key={props.entity.id}
          name={props.entity.name}
          display={props.display}
          onChoosenItem={props.onChoosenItem}
        />
      )}
    </>
  );
};

export default TreeList;
