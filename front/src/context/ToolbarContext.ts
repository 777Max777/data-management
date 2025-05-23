import React from "react";
import { Entity } from "../models/Entity";

export type PredicateType<T> = {
  predicate: (value: T, index: number, obj: T[]) => unknown;
};

export type UpdateDataParamsType<T> = {
  newData: T | T[];
  predicate?: (value: T, index: number, obj: T[]) => unknown;
};

export interface ModelToolContextProps<T> {
  data?: T | T[];
  updateData(
    newData: T | T[],
    predicate?: (value: T, index: number, obj: T[]) => unknown
  ): void;
  setCurrentData(
    data: T | T[],
    currentUpdateData: (
      newData: T | T[],
      predicate?: (value: T, index: number, obj: T[]) => unknown
    ) => void
  ): void;
}

const DEFAULT_VALUE_TREE_DATA: ModelToolContextProps<Entity> = {
  data: undefined,
  updateData: () => {},
  setCurrentData: () => {},
};

export const ModelToolContext = React.createContext<
  ModelToolContextProps<Entity>
>(DEFAULT_VALUE_TREE_DATA);
