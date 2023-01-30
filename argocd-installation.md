# Installation guide

Install via operator

## Expose ArgoCD in OpenShift

```yaml
apiVersion: argoproj.io/v1alpha1
kind: ArgoCD
metadata:
  name: demo-argocd
  namespace: argocd
spec:
  server:
    insecure: true
    route:
      enabled: true
```

## Get Username and Password

```
ARGOCD_NAMESPACE=argocd
kubectl -n $ARGOCD_NAMESPACE get secret demo-argocd-cluster -o jsonpath='{.data.admin\.password}' | base64 -d
```
USER is admin!