import React from "react";
import { Button } from "reactstrap";
import { ItemMode } from "../../hooks/crudProvider";

const styles = {
  button: {
    margin: "0 5px 5px 0",
  },
};

interface BaseToolbarProps {
  onChange(): void;
  onCreate(): void;
  onDelete(): void;
}
export function BaseToolbar(props: BaseToolbarProps) {
  return (
    <>
      <Button color="primary" style={styles.button} onClick={props.onCreate}>
        Создать
      </Button>
      <Button color="info" style={styles.button} onClick={props.onChange}>
        Изменить
      </Button>
      <Button color="danger" style={styles.button} onClick={props.onDelete}>
        Удалить
      </Button>
    </>
  );
}

export interface ButtonProps {
  setMode: (itemMode: ItemMode) => void
}
export function Toolbar({setMode}: ButtonProps) {
  return (
    <>
      <Button color="primary" style={styles.button} onClick={() => setMode(ItemMode.CREATE)}>
        Создать
      </Button>
      <Button color="info" style={styles.button} onClick={() => setMode(ItemMode.EDIT)}>
        Изменить
      </Button>
      <Button color="danger" style={styles.button} onClick={() => setMode(ItemMode.DELETE)}>
        Удалить
      </Button>
    </>
  );
}
