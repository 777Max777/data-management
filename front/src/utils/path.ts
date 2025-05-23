//export const EKP_PREFIX = process.env.REACT_APP_EKP_PREFIX || "";
//export const API_BASE_URL = process.env.REACT_APP_API_BASE_URL;

export enum PATH {
  HOME = "/",
  MODELS = "/models",
}

/*export const EKP_PATH: { [path in keyof typeof PATH]: string } = Object.keys(
  PATH
).reduce((paths, pathName) => {
  paths[pathName] = EKP_PREFIX + PATH[pathName];
  return paths;
}, {}) as any;*/
