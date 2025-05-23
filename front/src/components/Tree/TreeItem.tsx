import React from "react";
import "./TreeView.css";

type TreeItemProps = {
  id?: number
  name: string;
  display: boolean;
  onChoosenItem(a: HTMLAnchorElement): void;
};

export const TreeItem: React.FC<TreeItemProps> = ({
  id,
  name,
  display,
  onChoosenItem,
}) => {
  //const [isChecked, setChecked] = React.useState(false);
  const itemRef = React.useRef<HTMLAnchorElement>(null);

  const changeState = () => onChoosenItem(itemRef.current!);

  return (
    <li className={display ? "opened" : "closed"}>
      <a id={String(id)} ref={itemRef} href="#" onClick={changeState}>
        {name}
      </a>
    </li>
  );
};

export default TreeItem;
